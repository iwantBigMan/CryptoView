package com.crypto.cryptoview.domain.model

data class UpbitMarketTicker(
    val market: String,
    val tradePrice: Double,
    val change: String,
    val changeRate: Double,
    val signedChangeRate: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val accTradePrice24h: Double,
    val accTradeVolume24h: Double,
    val timestamp: Long
)