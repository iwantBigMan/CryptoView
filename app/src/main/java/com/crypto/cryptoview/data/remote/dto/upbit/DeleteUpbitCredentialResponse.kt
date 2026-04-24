package com.crypto.cryptoview.data.remote.dto.upbit

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUpbitCredentialResponse(
    val deleted: Boolean,
    val message: String
)