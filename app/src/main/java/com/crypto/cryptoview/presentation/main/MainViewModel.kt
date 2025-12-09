package com.crypto.cryptoview.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.usecase.BalanceCalculator
import com.crypto.cryptoview.domain.usecase.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.GetUpbitMTickerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUpbitAccountBalance: GetUpbitAccountBalancesUseCase,
    private val getUpbitMarketTicker: GetUpbitMTickerUseCase,
    private val balanceCalculator: BalanceCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val upbitResult = loadUpbitData()

                val allHoldings = upbitResult.holdings

                val topHoldings = allHoldings
                    .sortedByDescending { it.totalValue }
                    .take(5)

                val totalValue = upbitResult.totalValue
                val totalChange = allHoldings.sumOf { it.change }
                val totalChangeRate = if (totalValue > 0) {
                    (totalChange / (totalValue - totalChange)) * 100
                } else 0.0

                _uiState.value = MainUiState(
                    totalValue = totalValue,
                    totalChange = totalChange,
                    totalChangeRate = totalChangeRate,
                    topHoldings = topHoldings,
                    exchangeBreakdown = listOf(upbitResult.exchangeData),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun loadUpbitData(): BalanceCalculator.UpbitResult {
        val balancesResult = getUpbitAccountBalance()
        val balances = balancesResult.getOrElse {
            return BalanceCalculator.UpbitResult(
                totalValue = 0.0,
                holdings = emptyList(),
                exchangeData = ExchangeData(ExchangeType.UPBIT, 0.0)
            )
        }

        val tickers = getUpbitMarketTicker().getOrElse { emptyList() }

        return balanceCalculator.calculateUpbit(balances, tickers)
    }

    fun refresh() {
        loadData()
    }
}