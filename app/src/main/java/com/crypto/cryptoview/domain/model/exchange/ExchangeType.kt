package com.crypto.cryptoview.domain.model.exchange

enum class ExchangeType(val displayName: String) {
    UPBIT("Upbit"),
    BINANCE("Binance"),
    GATEIO("Gate.io"),
    BYBIT("Bybit")
}

enum class PositionSide {
    LONG,
    SHORT
}
