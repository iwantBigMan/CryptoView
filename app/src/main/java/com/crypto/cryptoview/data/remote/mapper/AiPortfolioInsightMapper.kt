package com.crypto.cryptoview.data.remote.mapper

import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioHoldingDto
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioInsightRequestDto
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioInsightResponseDto
import com.crypto.cryptoview.domain.model.ai.AiPortfolioHoldingSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSnapshot

fun AiPortfolioSnapshot.toRequestDto(): AiPortfolioInsightRequestDto {
    return AiPortfolioInsightRequestDto(
        baseCurrency = baseCurrency,
        totalValuationKrw = totalValuationKrw,
        totalPnlKrw = totalPnlKrw,
        totalPnlRate = totalPnlRate,
        holdings = holdings.map { it.toDto() }
    )
}

private fun AiPortfolioHoldingSnapshot.toDto(): AiPortfolioHoldingDto {
    return AiPortfolioHoldingDto(
        exchange = exchange,
        symbol = symbol,
        quantity = quantity,
        valuationKrw = valuationKrw,
        averagePrice = averagePrice,
        currentPrice = currentPrice,
        pnlKrw = pnlKrw,
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

