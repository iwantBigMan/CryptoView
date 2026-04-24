package com.crypto.cryptoview.presentation.component.holdingCoinView

import com.crypto.cryptoview.domain.model.ExchangeHoldingDetail

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
    val isLoading: Boolean = false,
    val error: String? = null
)
