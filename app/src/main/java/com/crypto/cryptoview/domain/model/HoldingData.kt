package com.crypto.cryptoview.domain.model

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