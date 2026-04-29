package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.model.auth.UpbitCredentialValidationResult
import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateAndSaveUpbitCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        accessKey: String,
        secretKey: String
    ): UpbitCredentialValidationResult {
        return authRepository.validateAndSaveUpbit(accessKey, secretKey)
    }
}
