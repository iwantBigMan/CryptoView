package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Gate.io API Key/Secret 검증 UseCase
 */
class ValidateGateCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(apiKey: String, secretKey: String): Boolean {
        return authRepository.validateGate(apiKey, secretKey)
    }
}
