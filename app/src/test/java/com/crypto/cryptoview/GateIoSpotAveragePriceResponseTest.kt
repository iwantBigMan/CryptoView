package com.crypto.cryptoview

import com.crypto.cryptoview.data.remote.dto.gateio.GateIoAveragePriceFees
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoSpotAveragePriceResponse
import com.crypto.cryptoview.data.remote.mapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class GateIoSpotAveragePriceResponseTest {

    @Test
    fun toDomain_keepsNumericStringsAndExposesBigDecimalValues() {
        val response = GateIoSpotAveragePriceResponse(
            currencyPair = "BTC_USDT",
            baseCurrency = "BTC",
            quoteCurrency = "USDT",
            quantity = "0.1499",
            currentQuantity = "0.1499",
            averagePrice = "55052.526263131565783",
            totalCost = "8252.3736868434217108",
            tradeCount = 3,
            fetchedPages = 1,
            fees = GateIoAveragePriceFees(
                baseCurrency = "0.0001",
                quoteCurrency = "8.5",
                other = emptyList()
            ),
            warnings = emptyList()
        )

        val domain = response.toDomain()

        assertEquals("55052.526263131565783", domain.averagePrice)
        assertEquals(BigDecimal("55052.526263131565783"), domain.averagePriceValue)
        assertEquals(BigDecimal("0.1499"), domain.currentQuantityValue)
        assertEquals(BigDecimal("8252.3736868434217108"), domain.totalCostValue)
    }
}
