package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.auth.CredentialDeletionResult
import com.crypto.cryptoview.domain.model.auth.CredentialValidationResult

interface AuthRepository {
    suspend fun validateAndSaveUpbit(
        accessKey: String,
        secretKey: String
    ): CredentialValidationResult

    suspend fun validateAndSaveGateIo(
        accessKey: String,
        secretKey: String
    ): CredentialValidationResult

    suspend fun deleteUpbitCredential(): CredentialDeletionResult

    suspend fun deleteGateIoCredential(): CredentialDeletionResult
}
