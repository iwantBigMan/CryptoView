package com.crypto.cryptoview.data.remote.mapper

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto
import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotTickerDto
import com.crypto.cryptoview.domain.model.gate.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker

fun GateSpotBalanceDto.toDomain(): GateSpotBalance {
    return GateSpotBalance(
        currency = currency,
        available = available.toDoubleOrNull() ?: 0.0,
        locked = locked.toDoubleOrNull() ?: 0.0
    )
}

fun GateSpotTickerDto.toDomain(): GateSpotTicker {
    return GateSpotTicker(
        symbol = symbol,
        lastPrice = last.toDoubleOrNull() ?: 0.0
    )
}
