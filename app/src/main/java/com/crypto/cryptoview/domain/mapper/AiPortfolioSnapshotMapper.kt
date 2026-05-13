package com.crypto.cryptoview.domain.mapper

import com.crypto.cryptoview.domain.model.ai.AiPortfolioHoldingSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSnapshot
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase

fun GetAllHoldingsUseCase.HoldingsResult.toAiPortfolioSnapshot(): AiPortfolioSnapshot {
    val totalPnlKrw = allHoldings.sumOf { it.change }
    val totalBuyValueKrw = totalValue - totalPnlKrw
    val totalPnlRate = if (totalBuyValueKrw > 0.0) {
        (totalPnlKrw / totalBuyValueKrw) * 100
    } else {
        0.0
    }

    return AiPortfolioSnapshot(
        baseCurrency = "KRW",
        totalValuationKrw = totalValue,
        totalPnlKrw = totalPnlKrw,
        totalPnlRate = totalPnlRate,
        holdings = allHoldings.map { holding ->
            val hasAveragePrice = holding.avgBuyPrice != null && holding.avgBuyPrice > 0.0
            AiPortfolioHoldingSnapshot(
                exchange = holding.exchange.displayName,
                symbol = holding.symbol,
                quantity = holding.balance,
                valuationKrw = holding.totalValue,
                averagePrice = holding.avgBuyPrice,
                currentPrice = holding.currentPrice,
                pnlKrw = holding.change.takeIf { hasAveragePrice },
                pnlRate = holding.changePercent.takeIf { hasAveragePrice }
            )
        }
    )
}

