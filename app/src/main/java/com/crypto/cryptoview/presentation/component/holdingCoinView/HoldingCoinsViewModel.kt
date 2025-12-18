package com.crypto.cryptoview.presentation.component.holdingCoinView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.domain.usecase.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitMTickerUseCase
import com.crypto.cryptoview.presentation.component.holdingCoinView.preview.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoldingCoinsViewModel @Inject constructor(
    private val getUpbitAccountBalance: GetUpbitAccountBalancesUseCase,
    private val getUpbitMarketTicker: GetUpbitMTickerUseCase,
    private val calculateBalanceUseCase: CalculateBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState.asStateFlow()

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
                loadHoldings()
                delay(1000)
            }
        }
    }

    fun stopAutoRefresh() {
        isAutoRefreshEnabled = false
        autoRefreshJob?.cancel()
    }

    /**
     * 보유 자산 데이터 로드
     */
    fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val balances = getUpbitAccountBalance().getOrNull() ?: emptyList()
                val tickers = getUpbitMarketTicker().getOrNull() ?: emptyList()

                val result = calculateBalanceUseCase.calculateUpbit(balances, tickers)

                _uiState.value = _uiState.value.copy(
                    allHoldings = result.holdings,
                    filteredHoldings = applyFilters(
                        result.holdings,
                        _uiState.value.searchQuery,
                        _uiState.value.sortType
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setHoldings(holdings: List<HoldingData>) {
        _uiState.value = _uiState.value.copy(
            allHoldings = holdings,
            filteredHoldings = applyFilters(holdings, _uiState.value.searchQuery, _uiState.value.sortType)
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredHoldings = applyFilters(_uiState.value.allHoldings, query, _uiState.value.sortType)
        )
    }

    fun onSortTypeChange(sortType: SortType) {
        _uiState.value = _uiState.value.copy(
            sortType = sortType,
            filteredHoldings = applyFilters(_uiState.value.allHoldings, _uiState.value.searchQuery, sortType)
        )
    }

    private fun applyFilters(
        holdings: List<HoldingData>,
        query: String,
        sortType: SortType
    ): List<HoldingData> {
        return holdings
            .filter { it.symbol.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true) }
            .let { filtered ->
                when (sortType) {
                    SortType.VALUE -> filtered.sortedByDescending { it.totalValue }
                    SortType.PROFIT -> filtered.sortedByDescending { it.changePercent }
                    SortType.SYMBOL -> filtered.sortedBy { it.symbol }
                }
            }
    }
}