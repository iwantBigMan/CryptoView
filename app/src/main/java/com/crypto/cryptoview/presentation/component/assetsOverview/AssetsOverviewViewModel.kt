package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.toForeignBalance
import com.crypto.cryptoview.domain.usecase.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotBalancesUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotTickersUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitMTickerUseCase
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
    private val getGateSpotBalance: GetGateSpotBalancesUseCase,
    private val getGateSpotTickers: GetGateSpotTickersUseCase,
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
        val upbitBalances = getUpbitAccountBalance().getOrElse {
            _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            return
        }
        val gateBalances = getGateSpotBalance().getOrElse {
            _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            return
        }
        val upbitTickers = getUpbitMarketTicker().getOrElse { emptyList() }

        // 게이트 거래소에서 필요한 코인 목록 추출
        val gateCurrencies = gateBalances.map { it.currency }.distinct()
        val gateTickers = getGateSpotTickers(gateCurrencies.joinToString(",")).getOrElse { emptyList() }

        // 게이트 시세 매핑 로직 수정
        val tickerMap = gateTickers.associate { ticker ->
            ticker.symbol to ticker.lastPrice // 필드 이름 확인 후 수정
        }

        // 업비트 계산
        val upbitResult = calculateBalanceUseCase.calculateUpbit(upbitBalances, upbitTickers)

        // 게이트 계산
        val gateResult = calculateBalanceUseCase.calculateForeign(
            balances = gateBalances.map { it.toForeignBalance() },
            tickers = tickerMap,
            usdtKrwRate = calculateBalanceUseCase.getUsdtKrwRate(upbitTickers),
            exchangeType = com.crypto.cryptoview.domain.model.ExchangeType.GATEIO
        )

        val totalResult = listOf(upbitResult, gateResult)
        val totalValue = totalResult.sumOf { it.totalValue }
        val totalChange = totalResult.sumOf { it.holdings.sumOf { holding -> holding.change } }
        val totalChangeRate = if (totalValue > 0) {
            (totalChange / (totalValue - totalChange)) * 100
        } else 0.0

        val topHoldings = totalResult.flatMap { it.holdings }
            .sortedByDescending { it.totalValue }
            .take(5)

        _uiState.value = MainUiState(
            totalValue = totalValue,
            totalChange = totalChange,
            totalChangeRate = totalChangeRate,
            topHoldings = topHoldings,
            exchangeBreakdown = totalResult.map { it.exchangeData },
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