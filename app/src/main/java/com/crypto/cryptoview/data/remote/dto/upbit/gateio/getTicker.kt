package com.crypto.cryptoview.data.remote.dto.upbit.gateio


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GateTickerDto(
    @SerialName("currency_pair")
    val symbol: String,
    val last: String
)
