
package com.crypto.cryptoview.domain.model.gate

import com.crypto.cryptoview.data.remote.dto.gateio.GateFuturesTickerDto

data class GateFuturesTicker(
    val contract: String,
    val lastPrice: Double,
    val markPrice: Double
)

fun GateFuturesTickerDto.toDomain(): GateFuturesTicker {
    return GateFuturesTicker(
        contract = contract,
        lastPrice = lastPrice.toDouble(),
        markPrice = markPrice.toDouble()
    )
}