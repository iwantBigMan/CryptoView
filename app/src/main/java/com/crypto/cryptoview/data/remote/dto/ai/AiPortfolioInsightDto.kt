package com.crypto.cryptoview.data.remote.dto.ai

import kotlinx.serialization.Serializable

@Serializable
data class AiPortfolioInsightRequestDto(
    val portfolioSummary: AiPortfolioSummaryDto,
    val holdings: List<AiPortfolioHoldingDto>
)

@Serializable
data class AiPortfolioSummaryDto(
    val baseCurrency: String,
    val holdingsCount: Int,
    val totalValuation: Double,
    val totalPnl: Double,
    val totalPnlRate: Double
)

@Serializable
data class AiPortfolioHoldingDto(
    val symbol: String,
    val market: String,
    val quantity: Double,
    val averagePrice: Double,
    val currentPrice: Double,
    val valuation: Double,
    val pnl: Double,
    val pnlRate: Double
)

@Serializable
data class AiPortfolioInsightResponseDto(
    val insight: String,
    val model: String
)

