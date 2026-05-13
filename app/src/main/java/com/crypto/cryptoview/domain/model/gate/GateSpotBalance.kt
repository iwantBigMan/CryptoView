package com.crypto.cryptoview.domain.model.gate

import com.crypto.cryptoview.domain.model.asset.ForeignBalance

data class GateSpotBalance(
    val currency: String,
    val available: Double,
    val locked: Double
)

fun GateSpotBalance.toForeignBalance(): ForeignBalance {
    return ForeignBalance(
        asset = currency,
        free = available,
        locked = locked
    )
}
