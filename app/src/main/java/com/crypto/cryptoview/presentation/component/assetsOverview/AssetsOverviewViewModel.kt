package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.model.toForeignBalance
import com.crypto.cryptoview.domain.usecase.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.calculator.BalanceCalculator
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotBalancesUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotTickersUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitMTickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            // 병렬 API 호출
            val upbitBalancesDeferred = viewModelScope.async { getUpbitAccountBalance() }
            val upbitTickersDeferred = viewModelScope.async { getUpbitMarketTicker() }
            val gateBalancesDeferred = viewModelScope.async { getGateSpotBalance() }

            val upbitBalances = upbitBalancesDeferred.await().getOrElse { emptyList() }
            val upbitTickers = upbitTickersDeferred.await().getOrElse { emptyList() }
            val gateBalances = gateBalancesDeferred.await().getOrElse { emptyList() }

            // Gate 티커 조회
            val tickerMap = fetchGateTickers(gateBalances.map { it.currency })

            // 잔고 계산
            val usdtKrwRate = calculateBalanceUseCase.getUsdtKrwRate(upbitTickers)
            val upbitResult = calculateBalanceUseCase.calculateUpbit(upbitBalances, upbitTickers)
            val gateResult = calculateBalanceUseCase.calculateForeign(
                balances = gateBalances.map { it.toForeignBalance() },
                tickers = tickerMap,
                usdtKrwRate = usdtKrwRate,
                exchangeType = ExchangeType.GATEIO
            )

            updateUiState(listOf(upbitResult, gateResult))
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
        }
    }

    private suspend fun fetchGateTickers(currencies: List<String>): Map<String, Double> {
        return currencies
            .filter { it != "USDT" && it != "POINT" }
            .distinct()
            .mapNotNull { currency ->
                getGateSpotTickers("${currency}_USDT").getOrNull()?.firstOrNull()
            }
            .associate { it.symbol.substringBefore("_") to it.lastPrice }
    }

    private fun updateUiState(results: List<BalanceCalculator.CalculationResult>) {
        val totalValue = results.sumOf { it.totalValue }
        val totalChange = results.sumOf { r -> r.holdings.sumOf { it.change } }
        val buyValue = totalValue - totalChange
        val totalChangeRate = if (buyValue > 0) (totalChange / buyValue) * 100 else 0.0

        _uiState.value = MainUiState(
            totalValue = totalValue,
            totalChange = totalChange,
            totalChangeRate = totalChangeRate,
            topHoldings = results.flatMap { it.holdings }.sortedByDescending { it.totalValue }.take(5),
            exchangeBreakdown = results.map { it.exchangeData },
            isLoading = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        isAutoRefreshEnabled = false
    }
}