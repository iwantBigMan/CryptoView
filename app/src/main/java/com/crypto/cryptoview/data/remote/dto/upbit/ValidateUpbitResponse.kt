package com.crypto.cryptoview.data.remote.dto.upbit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValidateUpbitResponse(
    @SerialName("valid")
    val valid: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("saved")
    val saved: Boolean? = null
)
