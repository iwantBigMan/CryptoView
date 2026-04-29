package com.crypto.cryptoview.data.remote.dto.gateio

import kotlinx.serialization.Serializable

@Serializable
data class GateIoValidateAndSaveRequest(
    val accessKey: String,
    val secretKey: String
)

@Serializable
data class GateIoValidateAndSaveResponse(
    val valid: Boolean,
    val message: String,
    val saved: Boolean? = null
)

@Serializable
data class GateIoCredentialDeleteResponse(
    val deleted: Boolean,
    val message: String
)
