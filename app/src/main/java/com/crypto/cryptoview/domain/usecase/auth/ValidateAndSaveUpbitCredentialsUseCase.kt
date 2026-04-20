package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateAndSaveUpbitCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository)
 {
    suspend operator fun invoke(accessKey: String, secretKey: String): ValidateUpbitResponse {
        return authRepository.validateAndSaveUpbit(accessKey, secretKey)
    }
}