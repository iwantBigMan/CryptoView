package com.crypto.cryptoview.presentation.model

import androidx.compose.ui.graphics.Color
import com.crypto.cryptoview.domain.model.exchange.ExchangeType

fun ExchangeType.uiColor(): Color {
    return when (this) {
        ExchangeType.UPBIT -> Color(0xFF2196F3)
        ExchangeType.BINANCE -> Color(0xFFFFC107)
        ExchangeType.GATEIO -> Color(0xFF9C27B0)
        ExchangeType.BYBIT -> Color(0xFFE91E63)
    }
}
