package com.crypto.cryptoview.data.remote.dto.gateio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GateFuturesTickerDto(
    @SerialName("contract")
    val contract: String,

    @SerialName("last")
    val lastPrice: String,

    @SerialName("mark_price")
    val markPrice: String
)
