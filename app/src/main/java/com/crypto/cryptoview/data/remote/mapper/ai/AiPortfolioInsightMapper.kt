package com.crypto.cryptoview.data.remote.mapper.ai

import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioHoldingDto
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioInsightRequestDto
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioInsightResponseDto
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioSummaryDto
import com.crypto.cryptoview.domain.model.ai.AiPortfolioHoldingSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSummarySnapshot

fun AiPortfolioSnapshot.toRequestDto(): AiPortfolioInsightRequestDto {
    return AiPortfolioInsightRequestDto(
        portfolioSummary = portfolioSummary.toDto(),
        holdings = holdings.map { it.toDto() }
    )
}

private fun AiPortfolioSummarySnapshot.toDto(): AiPortfolioSummaryDto {
    return AiPortfolioSummaryDto(
        baseCurrency = baseCurrency,
        holdingsCount = holdingsCount,
        totalValuation = totalValuation,
        totalPnl = totalPnl,
        totalPnlRate = totalPnlRate
    )
}

private fun AiPortfolioHoldingSnapshot.toDto(): AiPortfolioHoldingDto {
    return AiPortfolioHoldingDto(
        symbol = symbol,
        market = market,
        quantity = quantity,
        averagePrice = averagePrice,
        currentPrice = currentPrice,
        valuation = valuation,
        pnl = pnl,
        pnlRate = pnlRate
    )
}

fun AiPortfolioInsightResponseDto.toDomain(generatedAt: Long): AiPortfolioInsight {
    return AiPortfolioInsight(
        insight = insight,
        model = model,
        generatedAt = generatedAt
    )
}

