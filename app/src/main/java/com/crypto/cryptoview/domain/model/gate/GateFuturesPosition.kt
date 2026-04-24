package com.crypto.cryptoview.domain.model.gate

import com.crypto.cryptoview.data.remote.dto.gateio.GateFuturesPositionDto

data class GateFuturesPosition(
    val symbol: String,
    val size: Double,
    val entryPrice: Double,
    val leverage: Double,
    val unrealisedPnl: Double,
    val liquidationPrice: Double
)

fun GateFuturesPositionDto.toDomain(): GateFuturesPosition {
    return GateFuturesPosition(
        symbol = contract,
        size = size.toDouble(),
        entryPrice = entryPrice.toDouble(),
        leverage = leverage.toDouble(),
        unrealisedPnl = unrealisedPnl.toDouble(),
        liquidationPrice = 0.0 // 필요시 계산 로직 추가
    )
}