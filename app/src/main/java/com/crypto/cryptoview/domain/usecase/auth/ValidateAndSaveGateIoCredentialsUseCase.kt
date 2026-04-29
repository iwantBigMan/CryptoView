package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.model.auth.CredentialValidationResult
import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateAndSaveGateIoCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        accessKey: String,
        secretKey: String
    ): CredentialValidationResult {
        return authRepository.validateAndSaveGateIo(accessKey, secretKey)
    }
}
