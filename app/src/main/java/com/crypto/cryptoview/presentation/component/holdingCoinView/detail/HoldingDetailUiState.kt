package com.crypto.cryptoview.presentation.component.holdingCoinView.detail

import com.crypto.cryptoview.domain.model.asset.ExchangeHoldingDetail
import com.crypto.cryptoview.domain.model.gate.GateIoSpotAveragePrice

/**
 * 보유 상세 화면 UI 상태
 */
data class HoldingDetailUiState(
    val symbol: String = "",
    val coinName: String = "",
    val totalValueKrw: Double = 0.0,
    val totalProfitLoss: Double? = null,
    val totalProfitLossPercent: Double? = null,
    val exchangeHoldings: List<ExchangeHoldingDetail> = emptyList(),
    val gateIoAveragePriceState: GateIoAveragePriceUiState = GateIoAveragePriceUiState(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class GateIoAveragePriceUiState(
    val currencyPair: String? = null,
    val isLoading: Boolean = false,
    val data: GateIoSpotAveragePrice? = null,
    val errorMessage: String? = null,
    val errorType: GateIoAveragePriceErrorType? = null
)

enum class GateIoAveragePriceErrorType {
    REQUEST_ERROR,
    AUTH_ERROR,
    CREDENTIAL_NOT_FOUND,
    GATE_API_ERROR,
    UNKNOWN
}
