package com.crypto.cryptoview

import com.crypto.cryptoview.data.remote.dto.UpbitAccountBalanceDto
import org.junit.Assert
import org.junit.Test

class UpbitAccountBalanceDtoTest {

    @Test
    fun toDomain_shouldConvertSuccessfully() {
        // Given
        val dto = UpbitAccountBalanceDto(
            currency = "BTC",
            balance = "1.5",
            locked = "0.2",
            avgBuyPrice = "50000000",
            avgBuyPriceModified = true,
            unitCurrency = "KRW"
        )

        // When
        val domain = dto.toDomain()

        // Then
        Assert.assertEquals("BTC", domain.currency)
        Assert.assertEquals(1.5, domain.balance, 0.001)
        Assert.assertEquals(0.2, domain.locked, 0.001)
        Assert.assertEquals(50000000.0, domain.avgBuyPrice, 0.01)
        Assert.assertTrue(domain.avgBuyPriceModified)
        Assert.assertEquals("KRW", domain.unitCurrency)
    }

    @Test
    fun toDomain_shouldHandleInvalidNumbersAsZero() {
        // Given
        val dto = UpbitAccountBalanceDto(
            currency = "ETH",
            balance = "invalid",
            locked = "invalid",
            avgBuyPrice = "invalid",
            avgBuyPriceModified = false,
            unitCurrency = "KRW"
        )

        // When
        val domain = dto.toDomain()

        // Then
        Assert.assertEquals(0.0, domain.balance, 0.001)
        Assert.assertEquals(0.0, domain.locked, 0.001)
        Assert.assertEquals(0.0, domain.avgBuyPrice, 0.001)
    }
}