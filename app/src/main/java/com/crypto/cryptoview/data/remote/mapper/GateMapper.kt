package com.crypto.cryptoview.data.remote.mapper

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto
import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotTickerDto
import com.crypto.cryptoview.domain.model.gate.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

fun GateSpotBalanceDto.toDomain(): GateSpotBalance {
    return GateSpotBalance(
        currency = currency,
        available = available.toDoubleOrNull() ?: 0.0,
        locked = locked.toDoubleOrNull() ?: 0.0,
        avgBuyPriceUsdt = firstValidDouble(
            avgBuyPriceUsdt,
            avgBuyPriceUsdtCamel,
            avgBuyPrice,
            avgBuyPriceCamel,
            averageBuyPrice,
            averageBuyPriceCamel
        )
    )
}

fun GateSpotTickerDto.toDomain(): GateSpotTicker {
    return GateSpotTicker(
        symbol = symbol,
        lastPrice = last.toDoubleOrNull() ?: 0.0
    )
}

private fun firstValidDouble(vararg values: JsonElement?): Double {
    return values
        .asSequence()
        .mapNotNull { (it as? JsonPrimitive)?.content?.toDoubleOrNull() }
        .firstOrNull()
        ?: 0.0
}
