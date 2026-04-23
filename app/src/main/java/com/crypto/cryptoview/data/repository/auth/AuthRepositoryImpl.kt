package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.remote.api.ValidateAndSaveUpbit
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import com.crypto.cryptoview.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val validateAndSaveUpbit: ValidateAndSaveUpbit
) : AuthRepository {

    override suspend fun validateAndSaveUpbit(
        accessKey: String,
        secretKey: String
    ): ValidateUpbitResponse {
        return withContext(Dispatchers.IO) {
            // Authorization 헤더는 FirebaseAuthInterceptor 가 자동 주입
            validateAndSaveUpbit.validateAndSaveCredentials(
                request = ValidateUpbitRequest(accessKey, secretKey)
            )
        }
    }
}
