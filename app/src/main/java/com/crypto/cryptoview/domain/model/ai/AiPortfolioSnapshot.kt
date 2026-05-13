package com.crypto.cryptoview.domain.model.ai

data class AiPortfolioSnapshot(
    val baseCurrency: String,
    val totalValuationKrw: Double,
    val totalPnlKrw: Double,
    val totalPnlRate: Double,
    val holdings: List<AiPortfolioHoldingSnapshot>
)

data class AiPortfolioHoldingSnapshot(
    val exchange: String,
    val symbol: String,
    val quantity: Double,
    val valuationKrw: Double,
    val averagePrice: Double?,
    val currentPrice: Double?,
    val pnlKrw: Double?,
    val pnlRate: Double?
)

