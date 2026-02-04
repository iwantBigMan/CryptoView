package com.crypto.cryptoview.presentation.component.holdingCoinView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.domain.usecase.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.calculator.ExchangeRateProvider
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotBalancesUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotTickersUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitMTickerUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitTickerAllUseCase
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
    private val getUpbitTickerAll: GetUpbitTickerAllUseCase,
    private val getGateSpotBalances: GetGateSpotBalancesUseCase,
    private val getGateSpotTickers: GetGateSpotTickersUseCase,
    private val calculateBalanceUseCase: CalculateBalanceUseCase,
    private val exchangeRateProvider: ExchangeRateProvider
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
     * 보유 자산 데이터 로드 (업비트 + Gate.io 통합)
     */
    fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // 데이터 로드
                val upbitBalances = getUpbitAccountBalance().getOrNull() ?: emptyList()
                val upbitTickers = getUpbitMarketTicker().getOrNull() ?: emptyList()
                val upbitTickerAll = getUpbitTickerAll().getOrNull() ?: emptyList()
                val gateBalances = getGateSpotBalances().getOrNull() ?: emptyList()
                val gateTickers = getGateSpotTickers().getOrNull() ?: emptyList()

                // USDT/KRW 환율 계산
                val usdtKrwRate = exchangeRateProvider.getUsdtKrwRate(upbitTickers)

                android.util.Log.d("HoldingCoinsViewModel", "USDT/KRW Rate: $usdtKrwRate")

                // 전체 거래소 잔고 계산
                val result = calculateBalanceUseCase.calculateAll(
                    upbitBalances = upbitBalances,
                    upbitTickers = upbitTickers,
                    upbitAllTickers = upbitTickerAll,
                    gateioBalances = gateBalances,
                    gateioTickers = gateTickers
                )

                // 모든 거래소의 Holdings 통합
                val allHoldings = result.results.flatMap { it.holdings }
                    .filter { it.totalValue > 0 }  // 0원 이하 제외

                _uiState.value = _uiState.value.copy(
                    allHoldings = allHoldings,
                    filteredHoldings = applyFilters(
                        allHoldings,
                        _uiState.value.searchQuery,
                        _uiState.value.sortType
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                android.util.Log.e("HoldingCoinsViewModel", "Error loading holdings", e)
            }
        }
    }

    fun setHoldings(holdings: List<HoldingData>) {
        val filtered = holdings.filter { it.totalValue > 0 }  // 0원 이하 제외
        _uiState.value = _uiState.value.copy(
            allHoldings = filtered,
            filteredHoldings = applyFilters(filtered, _uiState.value.searchQuery, _uiState.value.sortType)
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
            .filter { it.totalValue > 1 }  // 0원 이하 제외
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