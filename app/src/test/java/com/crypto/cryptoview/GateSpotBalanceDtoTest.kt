package com.crypto.cryptoview

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto
import com.crypto.cryptoview.data.remote.mapper.toDomain
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class GateSpotBalanceDtoTest {

    @Test
    fun toDomain_mapsSnakeCaseAvgBuyPrice() {
        val dto = GateSpotBalanceDto(
            currency = "BTC",
            available = "0.25",
            locked = "0.05",
            avgBuyPrice = JsonPrimitive("65000.5")
        )

        val domain = dto.toDomain()

        assertEquals("BTC", domain.currency)
        assertEquals(0.25, domain.available, 0.000001)
        assertEquals(0.05, domain.locked, 0.000001)
        assertEquals(65000.5, domain.avgBuyPriceUsdt, 0.000001)
    }

    @Test
    fun toDomain_mapsCamelCaseAvgBuyPriceUsdt() {
        val dto = GateSpotBalanceDto(
            currency = "ETH",
            available = "1.5",
            locked = "0",
            avgBuyPriceUsdtCamel = JsonPrimitive(3200.25)
        )

        val domain = dto.toDomain()

        assertEquals(3200.25, domain.avgBuyPriceUsdt, 0.000001)
    }
}
