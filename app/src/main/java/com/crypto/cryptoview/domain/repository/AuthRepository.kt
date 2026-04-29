package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.auth.CredentialDeletionResult
import com.crypto.cryptoview.domain.model.auth.UpbitCredentialValidationResult

interface AuthRepository {
    suspend fun validateAndSaveUpbit(
        accessKey: String,
        secretKey: String
    ): UpbitCredentialValidationResult

    suspend fun deleteUpbitCredential(): CredentialDeletionResult
}
