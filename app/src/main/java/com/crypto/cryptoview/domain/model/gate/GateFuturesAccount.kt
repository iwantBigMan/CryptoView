
package com.crypto.cryptoview.domain.model.gate

import com.crypto.cryptoview.data.remote.dto.gateio.GateFuturesAccountDto

data class GateFuturesAccount(
    val total: Double,
    val available: Double,
    val unrealisedPnl: Double
)

fun GateFuturesAccountDto.toDomain(): GateFuturesAccount {
    return GateFuturesAccount(
        total = total.toDouble(),
        available = available.toDouble(),
        unrealisedPnl = unrealisedPnl.toDouble()
    )
}