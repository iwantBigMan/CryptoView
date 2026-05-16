package com.crypto.cryptoview

import com.crypto.cryptoview.domain.mapper.ai.toAiPortfolioSnapshot
import com.crypto.cryptoview.domain.model.asset.HoldingData
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AiPortfolioSnapshotMapperTest {

    @Test
    fun `AI snapshot keeps summary count and sends only holdings with average price`() {
        val result = GetAllHoldingsUseCase.HoldingsResult(
            allHoldings = listOf(
                HoldingData(
                    symbol = "BTC",
                    name = "BTC",
                    currentPrice = 100.0,
                    balance = 1.0,
                    totalValue = 100.0,
                    change = 10.0,
                    changePercent = 11.11,
                    exchange = ExchangeType.UPBIT,
                    avgBuyPrice = 90.0
                ),
                HoldingData(
                    symbol = "XRP",
                    name = "XRP",
                    currentPrice = 5.0,
                    balance = 20.0,
                    totalValue = 100.0,
                    change = 0.0,
                    changePercent = 0.0,
                    exchange = ExchangeType.GATEIO,
                    avgBuyPrice = null
                )
            ),
            aggregatedHoldings = emptyList(),
            exchangeResults = emptyList(),
            totalValue = 200.0,
            usdtKrwRate = 1400.0
        )

        val snapshot = result.toAiPortfolioSnapshot(DisplayCurrency.KRW)

        assertEquals("KRW", snapshot.portfolioSummary.baseCurrency)
        assertEquals(2, snapshot.portfolioSummary.holdingsCount)
        assertEquals(1, snapshot.holdings.size)
        assertEquals("BTC", snapshot.holdings.first().symbol)
        assertEquals("KRW-BTC", snapshot.holdings.first().market)
        assertEquals(90.0, snapshot.holdings.first().averagePrice, 0.0)
        assertNull(snapshot.holdings.find { it.symbol == "XRP" })
    }

    @Test
    fun `AI snapshot converts amounts to USDT when display currency is USDT`() {
        val result = GetAllHoldingsUseCase.HoldingsResult(
            allHoldings = listOf(
                HoldingData(
                    symbol = "BTC",
                    name = "BTC",
                    currentPrice = 140_000.0,
                    balance = 1.0,
                    totalValue = 140_000.0,
                    change = 14_000.0,
                    changePercent = 11.11,
                    exchange = ExchangeType.UPBIT,
                    avgBuyPrice = 126_000.0
                )
            ),
            aggregatedHoldings = emptyList(),
            exchangeResults = emptyList(),
            totalValue = 140_000.0,
            usdtKrwRate = 1_400.0
        )

        val snapshot = result.toAiPortfolioSnapshot(DisplayCurrency.USDT)

        assertEquals("USDT", snapshot.portfolioSummary.baseCurrency)
        assertEquals(100.0, snapshot.portfolioSummary.totalValuation, 0.0)
        assertEquals(10.0, snapshot.portfolioSummary.totalPnl, 0.0)
        assertEquals(90.0, snapshot.holdings.first().averagePrice, 0.0)
        assertEquals(100.0, snapshot.holdings.first().currentPrice, 0.0)
        assertEquals(100.0, snapshot.holdings.first().valuation, 0.0)
        assertEquals(10.0, snapshot.holdings.first().pnl, 0.0)
    }
}
