package com.crypto.cryptoview.data.remote.dto.gateio


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GateSpotTickerDto(
    @SerialName("currency_pair")
    val symbol: String,
    val last: String
)
