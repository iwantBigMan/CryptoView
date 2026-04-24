package com.crypto.cryptoview.data.remote.dto.upbit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValidateUpbitRequest(
    @SerialName("accessKey")
    val accessKey: String,
    @SerialName("secretKey")
    val secretKey: String
)
