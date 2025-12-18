package com.crypto.cryptoview.data.remote.dto.upbit.gateio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GateSpotBalanceDto(
    @SerialName("currency")
    val currency: String,

    @SerialName("available")
    val available: String,

    @SerialName("locked")
    val locked: String
)

@Serializable
data class GateFuturesAccountDto(
    @SerialName("total")
    val total: String,

    @SerialName("available")
    val available: String,

    @SerialName("unrealised_pnl")
    val unrealisedPnl: String
)
