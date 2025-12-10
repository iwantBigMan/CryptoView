package com.crypto.cryptoview.presentation.main

import androidx.compose.ui.graphics.Color

data class MainUiState(
    val totalValue: Double = 0.0,
    val totalChange: Double = 0.0,
    val totalChangeRate: Double = 0.0,
    val topHoldings: List<HoldingData> = emptyList(),
    val exchangeBreakdown: List<ExchangeData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class HoldingData(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val balance: Double,
    val totalValue: Double,
    val change: Double,
    val changePercent: Double,
    val exchange: ExchangeType
)

data class ExchangeData(
    val type: ExchangeType,
    val totalValue: Double
)

enum class ExchangeType(val displayName: String, val color: Color) {
    UPBIT("Upbit", Color(0xFF2196F3)),
    BINANCE("Binance", Color(0xFFFFC107)),
    GATEIO("Gate.io", Color(0xFF9C27B0)),
    BYBIT("Bybit", Color(0xFFE91E63))
}