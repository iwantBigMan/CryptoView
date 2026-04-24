package com.crypto.cryptoview.domain.model

import androidx.compose.ui.graphics.Color

enum class ExchangeType(val displayName: String, val color: Color) {
    UPBIT("Upbit", Color(0xFF2196F3)),
    BINANCE("Binance", Color(0xFFFFC107)),
    GATEIO("Gate.io", Color(0xFF9C27B0)),
    BYBIT("Bybit", Color(0xFFE91E63))
}

enum class PositionSide {
    LONG,
    SHORT
}