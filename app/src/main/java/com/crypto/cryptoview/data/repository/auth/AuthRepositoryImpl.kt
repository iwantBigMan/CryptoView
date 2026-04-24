package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.auth.FirebaseTokenProvider
import com.crypto.cryptoview.data.remote.api.DeleteUpbitCredentials
import com.crypto.cryptoview.data.remote.api.ValidateAndSaveUpbit
import com.crypto.cryptoview.data.remote.dto.upbit.DeleteUpbitCredentialResponse
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import com.crypto.cryptoview.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val validateAndSaveUpbit: ValidateAndSaveUpbit,
    private val deleteUpbitCredentials: DeleteUpbitCredentials,
    private val tokenProvider: FirebaseTokenProvider
) : AuthRepository {

    override suspend fun validateAndSaveUpbit(
        accessKey: String,
        secretKey: String
    ): ValidateUpbitResponse {
        return withContext(Dispatchers.IO) {
            validateAndSaveUpbit.validateAndSaveCredentials(
                request = ValidateUpbitRequest(accessKey, secretKey)
            )
        }
    }

    override suspend fun deleteUpbitCredential(): DeleteUpbitCredentialResponse {
        return withContext(Dispatchers.IO) {
            val token = tokenProvider.getIdToken() ?: throw Exception("Firebase 토큰 없음")
            deleteUpbitCredentials.deleteUpbitCredential("Bearer $token")
        }
    }
}
