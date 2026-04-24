// File: 'app/src/main/java/com/crypto/cryptoview/domain/model/GateSpotBalance.kt'
package com.crypto.cryptoview.domain.model

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto

data class GateSpotBalance(
    val currency: String,
    val available: Double,
    val locked: Double
)


fun GateSpotBalanceDto.toDomain(): GateSpotBalance {
    return GateSpotBalance(
        currency = currency,
        available = available.toDouble(),
        locked = locked.toDouble()
    )
}

fun GateSpotBalance.toForeignBalance(): ForeignBalance {
    return ForeignBalance(
        asset = currency,
        free = available,
        locked = locked
    )
}