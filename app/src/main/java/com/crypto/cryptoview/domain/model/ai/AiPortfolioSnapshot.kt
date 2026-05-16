package com.crypto.cryptoview.domain.model.ai

data class AiPortfolioSnapshot(
    val portfolioSummary: AiPortfolioSummarySnapshot,
    val holdings: List<AiPortfolioHoldingSnapshot>
)

data class AiPortfolioSummarySnapshot(
    val baseCurrency: String,
    val holdingsCount: Int,
    val totalValuation: Double,
    val totalPnl: Double,
    val totalPnlRate: Double
)

data class AiPortfolioHoldingSnapshot(
    val symbol: String,
    val market: String,
    val quantity: Double,
    val averagePrice: Double,
    val currentPrice: Double,
    val valuation: Double,
    val pnl: Double,
    val pnlRate: Double
)

