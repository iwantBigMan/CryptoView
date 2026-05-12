package com.crypto.cryptoview.domain.model.gate

import com.crypto.cryptoview.domain.model.asset.ForeignBalance

data class GateSpotBalance(
    val currency: String,
    val available: Double,
    val locked: Double,
    val avgBuyPriceUsdt: Double = 0.0
)

fun GateSpotBalance.toForeignBalance(): ForeignBalance {
    return ForeignBalance(
        asset = currency,
        free = available,
        locked = locked,
        avgBuyPriceUsdt = avgBuyPriceUsdt
    )
}
