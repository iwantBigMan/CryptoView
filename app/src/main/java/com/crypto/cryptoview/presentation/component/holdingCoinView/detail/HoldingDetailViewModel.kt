package com.crypto.cryptoview.presentation.component.holdingCoinView.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.crypto.cryptoview.domain.model.asset.CurrencyUnit
import com.crypto.cryptoview.domain.model.asset.ExchangeHoldingDetail
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.model.gate.GateIoSpotAveragePrice
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase
import com.crypto.cryptoview.domain.usecase.GetExchangeHoldingDetailsUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateIoSpotAveragePriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * 보유 상세 화면 ViewModel
 */
@HiltViewModel
class HoldingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAllHoldingsUseCase: GetAllHoldingsUseCase,
    private val getExchangeHoldingDetailsUseCase: GetExchangeHoldingDetailsUseCase,
    private val getGateIoSpotAveragePriceUseCase: GetGateIoSpotAveragePriceUseCase
) : ViewModel() {

    private companion object {
        const val TAG = "HoldingDetailVM"
    }

    private val _uiState = MutableStateFlow(HoldingDetailUiState())
    val uiState: StateFlow<HoldingDetailUiState> = _uiState.asStateFlow()

    private var currentSymbol: String = savedStateHandle["symbol"] ?: ""

    init {
        if (currentSymbol.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(symbol = currentSymbol)
            loadData()
        }
    }

    private fun loadData() {
        if (currentSymbol.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                gateIoAveragePriceState = GateIoAveragePriceUiState()
            )

            try {
                val holdingsResult = getAllHoldingsUseCase(minValue = 0.0)

                holdingsResult.onSuccess { result ->
                    val detailResult = getExchangeHoldingDetailsUseCase(
                        symbol = currentSymbol,
                        allHoldings = result.allHoldings,
                        usdtKrwRate = result.usdtKrwRate
                    )

                    _uiState.value = _uiState.value.copy(
                        symbol = detailResult.symbol,
                        coinName = detailResult.coinName,
                        totalValueKrw = detailResult.totalValueKrw,
                        totalProfitLoss = detailResult.totalProfitLoss,
                        totalProfitLossPercent = detailResult.totalProfitLossPercent,
                        usdtKrwRate = result.usdtKrwRate,
                        exchangeHoldings = detailResult.exchangeHoldings,
                        isLoading = false,
                        error = null
                    )

                    loadGateIoAveragePriceIfNeeded(usdtKrwRate = result.usdtKrwRate)
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "데이터 로드 실패"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "알 수 없는 오류"
                )
            }
        }
    }

    private fun loadGateIoAveragePriceIfNeeded(usdtKrwRate: Double) {
        val gateHolding = _uiState.value.exchangeHoldings
            .firstOrNull { it.exchange == ExchangeType.GATEIO }
            ?: run {
                _uiState.value = _uiState.value.copy(
                    gateIoAveragePriceState = GateIoAveragePriceUiState()
                )
                return
            }

        val currencyPair = "${gateHolding.symbol.uppercase()}_USDT"
        viewModelScope.launch {
            Log.d(
                TAG,
                "Gate.io spot-average-price request: currencyPair=$currencyPair, symbol=${gateHolding.symbol}"
            )

            _uiState.value = _uiState.value.copy(
                gateIoAveragePriceState = GateIoAveragePriceUiState(
                    currencyPair = currencyPair,
                    isLoading = true
                )
            )

            getGateIoSpotAveragePriceUseCase(currencyPair = currencyPair)
                .onSuccess { averagePrice ->
                    Log.d(
                        TAG,
                        "Gate.io spot-average-price success: " +
                            "currencyPair=${averagePrice.currencyPair}, " +
                            "averagePrice=${averagePrice.averagePrice}, " +
                            "totalCost=${averagePrice.totalCost}, " +
                            "quantity=${averagePrice.quantity}, " +
                            "currentQuantity=${averagePrice.currentQuantity}, " +
                            "tradeCount=${averagePrice.tradeCount}, " +
                            "fetchedPages=${averagePrice.fetchedPages}, " +
                            "warnings=${averagePrice.warnings}"
                    )

                    val updatedHoldings = applyGateIoAveragePrice(
                        holdings = _uiState.value.exchangeHoldings,
                        averagePrice = averagePrice,
                        usdtKrwRate = usdtKrwRate
                    )
                    _uiState.value = _uiState.value.copy(
                        exchangeHoldings = updatedHoldings,
                        totalValueKrw = updatedHoldings.sumOf { it.valueKrw },
                        totalProfitLoss = calculateTotalProfitLoss(updatedHoldings),
                        totalProfitLossPercent = calculateTotalProfitLossPercent(updatedHoldings),
                        gateIoAveragePriceState = GateIoAveragePriceUiState(
                            currencyPair = currencyPair,
                            data = averagePrice
                        )
                    )
                }
                .onFailure { throwable ->
                    Log.e(
                        TAG,
                        "Gate.io spot-average-price failure: currencyPair=$currencyPair",
                        throwable
                    )

                    _uiState.value = _uiState.value.copy(
                        gateIoAveragePriceState = GateIoAveragePriceUiState(
                            currencyPair = currencyPair,
                            errorMessage = throwable.toGateIoAveragePriceMessage(),
                            errorType = throwable.toGateIoAveragePriceErrorType()
                        )
                    )
                }
        }
    }

    private fun applyGateIoAveragePrice(
        holdings: List<ExchangeHoldingDetail>,
        averagePrice: GateIoSpotAveragePrice,
        usdtKrwRate: Double
    ): List<ExchangeHoldingDetail> {
        val averagePriceUsdt = averagePrice.averagePriceValue?.toDouble()
        val totalCostUsdt = averagePrice.totalCostValue?.toDouble()
        val currentQuantity = averagePrice.currentQuantityValue?.toDouble()

        return holdings.map { holding ->
            if (holding.exchange != ExchangeType.GATEIO) return@map holding

            val displayQuantity = currentQuantity?.takeIf { it > 0.0 } ?: holding.quantity
            val currentPriceKrw = holding.currentPrice * usdtKrwRate
            val currentValueKrw = displayQuantity * currentPriceKrw
            val hasAveragePrice = averagePriceUsdt != null && averagePriceUsdt > 0.0
            val hasTotalCost = totalCostUsdt != null && totalCostUsdt > 0.0
            val profitLossUsdt = if (hasTotalCost) {
                (displayQuantity * holding.currentPrice) - totalCostUsdt
            } else null
            val profitLossKrw = profitLossUsdt?.let { it * usdtKrwRate }
            val profitLossPercent = if (profitLossUsdt != null && totalCostUsdt != null && totalCostUsdt > 0.0) {
                (profitLossUsdt / totalCostUsdt) * 100
            } else null

            holding.copy(
                quantity = displayQuantity,
                avgBuyPrice = averagePriceUsdt?.takeIf { hasAveragePrice }?.let { it * usdtKrwRate },
                currentPrice = currentPriceKrw,
                currencyUnit = CurrencyUnit.KRW,
                valueKrw = currentValueKrw,
                profitLoss = profitLossKrw,
                profitLossPercent = profitLossPercent
            )
        }
    }

    private fun calculateTotalProfitLoss(holdings: List<ExchangeHoldingDetail>): Double? {
        return holdings
            .mapNotNull { it.profitLoss }
            .takeIf { it.isNotEmpty() }
            ?.sum()
    }

    private fun calculateTotalProfitLossPercent(holdings: List<ExchangeHoldingDetail>): Double? {
        val totalProfitLoss = calculateTotalProfitLoss(holdings) ?: return null
        val totalValue = holdings.sumOf { it.valueKrw }
        val totalBuyValue = totalValue - totalProfitLoss
        return if (totalBuyValue > 0.0) {
            (totalProfitLoss / totalBuyValue) * 100
        } else {
            null
        }
    }

    fun setSymbol(newSymbol: String) {
        if (newSymbol.isNotEmpty() && newSymbol != currentSymbol) {
            currentSymbol = newSymbol
            _uiState.value = _uiState.value.copy(symbol = newSymbol)
            loadData()
        } else if (newSymbol.isNotEmpty() && _uiState.value.exchangeHoldings.isEmpty() && !_uiState.value.isLoading) {
            loadData()
        }
    }

    fun refresh() {
        loadData()
    }

    private fun Throwable.toGateIoAveragePriceErrorType(): GateIoAveragePriceErrorType {
        return when ((this as? HttpException)?.code()) {
            400 -> GateIoAveragePriceErrorType.REQUEST_ERROR
            401 -> GateIoAveragePriceErrorType.AUTH_ERROR
            404 -> GateIoAveragePriceErrorType.CREDENTIAL_NOT_FOUND
            502 -> GateIoAveragePriceErrorType.GATE_API_ERROR
            else -> GateIoAveragePriceErrorType.UNKNOWN
        }
    }

    private fun Throwable.toGateIoAveragePriceMessage(): String {
        return when (toGateIoAveragePriceErrorType()) {
            GateIoAveragePriceErrorType.REQUEST_ERROR -> "Gate.io 평균단가 요청 형식이 올바르지 않습니다"
            GateIoAveragePriceErrorType.AUTH_ERROR -> "로그인이 만료되었습니다. 다시 로그인해 주세요"
            GateIoAveragePriceErrorType.CREDENTIAL_NOT_FOUND -> "저장된 Gate.io 키가 없습니다. 설정에서 Gate.io 키를 등록해 주세요"
            GateIoAveragePriceErrorType.GATE_API_ERROR -> "Gate.io 평균단가 계산에 실패했습니다. 잠시 후 다시 시도해 주세요"
            GateIoAveragePriceErrorType.UNKNOWN -> message ?: "Gate.io 평균단가 조회에 실패했습니다"
        }
    }
}
