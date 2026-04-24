package com.crypto.cryptoview.domain.model.gate

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotTickerDto

data class GateSpotTicker(
    val symbol: String,
    val lastPrice: Double
)

fun GateSpotTickerDto.toDomain(): GateSpotTicker {
    return GateSpotTicker(
        symbol = symbol,
        lastPrice = last.toDouble()
    )
}