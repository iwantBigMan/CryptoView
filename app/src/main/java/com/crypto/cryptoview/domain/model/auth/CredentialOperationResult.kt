package com.crypto.cryptoview.domain.model.auth

data class UpbitCredentialValidationResult(
    val valid: Boolean,
    val message: String,
    val saved: Boolean
)

data class CredentialDeletionResult(
    val deleted: Boolean,
    val message: String
)
