package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 업비트 API Key/Secret 검증 UseCase
 */
class ValidateUpbitCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(apiKey: String, secretKey: String): Boolean {
        return authRepository.validateUpbit(apiKey, secretKey)
    }
}
