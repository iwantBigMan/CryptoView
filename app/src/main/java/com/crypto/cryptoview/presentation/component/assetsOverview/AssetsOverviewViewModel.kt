package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.usecase.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.calculator.ExchangeRateProvider
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotBalancesUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotTickersUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitMTickerUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitTickerAllUseCase
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
class AssetsOverviewViewModel @Inject constructor(
    private val getUpbitAccountBalance: GetUpbitAccountBalancesUseCase,
    private val getUpbitMarketTicker: GetUpbitMTickerUseCase,
    private val getUpbitTickerAll: GetUpbitTickerAllUseCase,
    private val getGateSpotBalances: GetGateSpotBalancesUseCase,
    private val getGateSpotTickers: GetGateSpotTickersUseCase,
    private val calculateBalanceUseCase: CalculateBalanceUseCase,
    private val exchangeRateProvider: ExchangeRateProvider
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
                loadAssets()
                delay(1000)
            }
        }
    }

    fun stopAutoRefresh() {
        isAutoRefreshEnabled = false
        autoRefreshJob?.cancel()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val upbitBalances = getUpbitAccountBalance().getOrNull() ?: emptyList()
                val upbitTickers = getUpbitMarketTicker().getOrNull() ?: emptyList()
                val upbitTickerAll = getUpbitTickerAll().getOrNull() ?: emptyList()
                val gateBalances = getGateSpotBalances().getOrNull() ?: emptyList()
                val gateTickers = getGateSpotTickers().getOrNull() ?: emptyList()

                val usdtKrwRate = exchangeRateProvider.getUsdtKrwRate(upbitTickers)
                android.util.Log.d("AssetsOverview", "USDT/KRW Rate: $usdtKrwRate")

                val result = calculateBalanceUseCase.calculateAll(
                    upbitBalances = upbitBalances,
                    upbitTickers = upbitTickers,
                    upbitAllTickers = upbitTickerAll,
                    gateioBalances = gateBalances,
                    gateioTickers = gateTickers
                )

                // ViewModel에서 필터링, 정렬, Top 5 추출
                val allHoldings = result.results
                    .flatMap { it.holdings }
                    .filter { it.totalValue > 1.0 }
                    .sortedByDescending { it.totalValue }

                _uiState.value = _uiState.value.copy(
                    totalValue = result.totalValue,
                    totalChange = result.results.sumOf { res ->
                        res.holdings.sumOf { it.change }
                    },
                    totalChangeRate = calculateTotalChangeRate(result),
                    topHoldings = allHoldings.take(5),  // ← Top 5만 저장
                    exchangeBreakdown = result.results.map { it.exchangeData },
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                android.util.Log.e("AssetsOverview", "Error loading assets", e)
            }
        }
    }

    private fun calculateTotalChangeRate(result: CalculateBalanceUseCase.TotalBalanceResult): Double {
        val totalBuyValue = result.results.flatMap { it.holdings }
            .sumOf { it.totalValue - it.change }

        return if (totalBuyValue > 0) {
            (result.totalValue - totalBuyValue) / totalBuyValue * 100
        } else {
            0.0
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}