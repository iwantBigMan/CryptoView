package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.usecase.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.GetUpbitMTickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 자산 개요 화면의 ViewModel
 * UseCase만 호출, UI 상태 관리만 담당
 */
@HiltViewModel
class AssetsOverviewViewModel @Inject constructor(
    private val getUpbitAccountBalance: GetUpbitAccountBalancesUseCase,
    private val getUpbitMarketTicker: GetUpbitMTickerUseCase,
    private val calculateBalanceUseCase: CalculateBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null
    private var isAutoRefreshEnabled = true

    init {
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        if (autoRefreshJob?.isActive == true) return

        isAutoRefreshEnabled = true

        autoRefreshJob = viewModelScope.launch {
            while (isActive && isAutoRefreshEnabled) {
                loadData()
                delay(5000)
            }
        }
    }

    fun stopAutoRefresh() {
        isAutoRefreshEnabled = false
        autoRefreshJob?.cancel()
    }

    private suspend fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val balances = getUpbitAccountBalance().getOrElse {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                return
            }

            val tickers = getUpbitMarketTicker().getOrElse { emptyList() }

            // UseCase를 통해 계산
            val result = calculateBalanceUseCase.calculateUpbit(balances, tickers)

            val topHoldings = result.holdings
                .sortedByDescending { it.totalValue }
                .take(5)

            val totalChange = result.holdings.sumOf { it.change }
            val totalChangeRate = if (result.totalValue > 0) {
                (totalChange / (result.totalValue - totalChange)) * 100
            } else 0.0

            _uiState.value = MainUiState(
                totalValue = result.totalValue,
                totalChange = totalChange,
                totalChangeRate = totalChangeRate,
                topHoldings = topHoldings,
                exchangeBreakdown = listOf(result.exchangeData),
                isLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        isAutoRefreshEnabled = false
    }
}