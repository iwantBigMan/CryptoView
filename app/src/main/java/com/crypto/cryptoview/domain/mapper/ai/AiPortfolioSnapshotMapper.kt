package com.crypto.cryptoview.domain.mapper.ai

import com.crypto.cryptoview.domain.model.ai.AiPortfolioHoldingSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSummarySnapshot
import com.crypto.cryptoview.domain.model.asset.HoldingData
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase

fun GetAllHoldingsUseCase.HoldingsResult.toAiPortfolioSnapshot(
    displayCurrency: DisplayCurrency
): AiPortfolioSnapshot {
    fun displayAmount(amountKrw: Double): Double {
        return when (displayCurrency) {
            DisplayCurrency.KRW -> amountKrw
            DisplayCurrency.USDT -> if (usdtKrwRate > 0.0) amountKrw / usdtKrwRate else amountKrw
        }
    }

    val totalPnlKrw = allHoldings.sumOf { it.change }
    val totalBuyValueKrw = totalValue - totalPnlKrw
    val totalPnlRate = if (totalBuyValueKrw > 0.0) {
        (totalPnlKrw / totalBuyValueKrw) * 100
    } else {
        0.0
    }

    return AiPortfolioSnapshot(
        portfolioSummary = AiPortfolioSummarySnapshot(
            baseCurrency = displayCurrency.name,
            holdingsCount = allHoldings.size,
            totalValuation = displayAmount(totalValue),
            totalPnl = displayAmount(totalPnlKrw),
            totalPnlRate = totalPnlRate
        ),
        holdings = allHoldings
            .filter { holding -> holding.avgBuyPrice != null && holding.avgBuyPrice > 0.0 }
            .map { holding ->
                AiPortfolioHoldingSnapshot(
                    symbol = holding.symbol,
                    market = holding.toMarketName(),
                    quantity = holding.balance,
                    averagePrice = displayAmount(requireNotNull(holding.avgBuyPrice)),
                    currentPrice = displayAmount(holding.currentPrice),
                    valuation = displayAmount(holding.totalValue),
                    pnl = displayAmount(holding.change),
                    pnlRate = holding.changePercent
                )
            }
    )
}

private fun HoldingData.toMarketName(): String {
    return when (exchange) {
        ExchangeType.UPBIT -> "KRW-${symbol.uppercase()}"
        ExchangeType.GATEIO -> "${symbol.uppercase()}_USDT"
        ExchangeType.BINANCE -> "${symbol.uppercase()}USDT"
        ExchangeType.BYBIT -> "${symbol.uppercase()}USDT"
    }
}

