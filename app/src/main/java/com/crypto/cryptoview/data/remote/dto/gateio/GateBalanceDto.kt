package com.crypto.cryptoview.data.remote.dto.gateio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GateSpotBalanceDto(
    @SerialName("currency")
    val currency: String,

    @SerialName("available")
    val available: String,

    @SerialName("locked")
    val locked: String,

    @SerialName("update_id")
    val updateId: Long? = null,

    @SerialName("refresh_time")
    val refreshTime: Long? = null,
)
