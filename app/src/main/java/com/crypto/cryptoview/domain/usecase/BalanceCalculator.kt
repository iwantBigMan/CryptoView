package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import com.crypto.cryptoview.presentation.component.assetsOverview.ExchangeData
import com.crypto.cryptoview.presentation.component.assetsOverview.ExchangeType
import com.crypto.cryptoview.presentation.component.assetsOverview.HoldingData
import javax.inject.Inject

class BalanceCalculator @Inject constructor() {

    data class UpbitResult(
        val totalValue: Double,
        val holdings: List<HoldingData>,
        val exchangeData: ExchangeData
    )

    fun calculateUpbit(
        balances: List<UpbitAccountBalance>,
        tickers: List<UpbitMarketTicker>
    ): UpbitResult {
        if (balances.isEmpty()) {
            return UpbitResult(
                totalValue = 0.0,
                holdings = emptyList(),
                exchangeData = ExchangeData(ExchangeType.UPBIT, 0.0)
            )
        }

        val krwBalance = balances.find { it.currency == "KRW" }?.balance ?: 0.0

        val cryptoCurrentValue = balances
            .filter { it.currency != "KRW" }
            .sumOf { balance ->
                val currentPrice = getCurrentPrice(balance.currency, tickers) ?: 0.0
                balance.balance * currentPrice
            }

        val totalValue = krwBalance + cryptoCurrentValue

        val holdings = calculateHoldings(balances, tickers, ExchangeType.UPBIT)

        return UpbitResult(
            totalValue = totalValue,
            holdings = holdings,
            exchangeData = ExchangeData(ExchangeType.UPBIT, totalValue)
        )
    }

    private fun calculateHoldings(
        balances: List<UpbitAccountBalance>,
        tickers: List<UpbitMarketTicker>,
        exchangeType: ExchangeType
    ): List<HoldingData> {
        return balances
            .filter { it.currency != "KRW" && it.balance > 0 }
            .mapNotNull { balance ->
                val currentPrice = getCurrentPrice(balance.currency, tickers) ?: return@mapNotNull null
                val totalValue = balance.balance * currentPrice
                val buyValue = balance.balance * balance.avgBuyPrice
                val change = totalValue - buyValue
                val changePercent = if (buyValue > 0) (change / buyValue) * 100 else 0.0

                HoldingData(
                    symbol = balance.currency,
                    name = balance.currency,
                    currentPrice = currentPrice,
                    balance = balance.balance,
                    totalValue = totalValue,
                    change = change,
                    changePercent = changePercent,
                    exchange = exchangeType
                )
            }
            .sortedByDescending { it.totalValue }
    }

    private fun getCurrentPrice(currency: String, tickers: List<UpbitMarketTicker>): Double? {
        return tickers.find { it.market == "KRW-$currency" }?.tradePrice
    }
}