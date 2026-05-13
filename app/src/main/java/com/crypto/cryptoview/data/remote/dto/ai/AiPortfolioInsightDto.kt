package com.crypto.cryptoview.data.remote.dto.ai

import kotlinx.serialization.Serializable

@Serializable
data class AiPortfolioInsightRequestDto(
    val baseCurrency: String,
    val totalValuationKrw: Double,
    val totalPnlKrw: Double,
    val totalPnlRate: Double,
    val holdings: List<AiPortfolioHoldingDto>
)

@Serializable
data class AiPortfolioHoldingDto(
    val exchange: String,
    val symbol: String,
    val quantity: Double,
    val valuationKrw: Double,
    val averagePrice: Double? = null,
    val currentPrice: Double? = null,
    val pnlKrw: Double? = null,
    val pnlRate: Double? = null
)

@Serializable
data class AiPortfolioInsightResponseDto(
    val insight: String,
    val model: String
)

