package com.crypto.cryptoview.data.remote.dto

import com.crypto.cryptoview.data.remote.dto.upbit.UpbitAccountBalanceDto
import org.junit.Assert.*
import org.junit.Test

class UpbitAccountBalanceDtoTest {

    @Test
    fun toDomain_shouldConvertSuccessfully() {
        // Given - 정상적인 DTO 데이터
        val dto = UpbitAccountBalanceDto(
            currency = "BTC",
            balance = "1.5",
            locked = "0.2",
            avgBuyPrice = "50000000",
            avgBuyPriceModified = true,
            unitCurrency = "KRW"
        )

        // When - Domain 객체로 변환
        val domain = dto.toDomain()

        // Then - 모든 필드가 정확히 변환되었는지 검증
        assertEquals("BTC", domain.currency)
        assertEquals(1.5, domain.balance, 0.001)
        assertEquals(0.2, domain.locked, 0.001)
        assertEquals(50000000.0, domain.avgBuyPrice, 0.01)
        assertTrue(domain.avgBuyPriceModified)
        assertEquals("KRW", domain.unitCurrency)
    }

    @Test
    fun toDomain_shouldReturnZeroForInvalidNumbers() {
        // Given - 숫자 파싱 실패할 데이터
        val dto = UpbitAccountBalanceDto(
            currency = "ETH",
            balance = "invalid",
            locked = "invalid",
            avgBuyPrice = "invalid",
            avgBuyPriceModified = false,
            unitCurrency = "KRW"
        )

        // When - Domain 객체로 변환
        val domain = dto.toDomain()

        // Then - 파싱 실패 시 0으로 처리되는지 검증
        assertEquals(0.0, domain.balance, 0.001)
        assertEquals(0.0, domain.locked, 0.001)
        assertEquals(0.0, domain.avgBuyPrice, 0.001)
    }
}