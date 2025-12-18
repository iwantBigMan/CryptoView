package com.crypto.cryptoview.data.remote.dto.upbit.gateio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GateFuturesPositionDto(
    @SerialName("contract")
    val contract: String,

    @SerialName("size")
    val size: Int,

    @SerialName("entry_price")
    val entryPrice: String,

    @SerialName("mark_price")
    val markPrice: String,

    @SerialName("unrealised_pnl")
    val unrealisedPnl: String,

    @SerialName("leverage")
    val leverage: String,

    @SerialName("mode")
    val marginMode: String
)
