package com.crypto.cryptoview.domain.model



data class UpbitTickerAll(
    val market: String,
    val tradeDate: String,
    val tradeTime: String,
    val tradeDateKst: String,
    val tradeTimeKst: String,
    val tradeTimestamp: Long,
    val openingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val tradePrice: Double,
    val prevClosingPrice: Double,
    val change: String,
    val changePrice: Double,
    val changeRate: Double,
    val signedChangePrice: Double,
    val signedChangeRate: Double,
    val tradeVolume: Double,
    val accTradePrice: Double,
    val accTradePrice24h: Double,
    val accTradeVolume: Double,
    val accTradeVolume24h: Double,
    val highest52WeekPrice: Double,
    val highest52WeekDate: String,
    val lowest52WeekPrice: Double,
    val lowest52WeekDate: String,
    val timestamp: Long
)