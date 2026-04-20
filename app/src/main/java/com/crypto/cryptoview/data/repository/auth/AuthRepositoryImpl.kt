package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.remote.api.ValidateUpbitApi
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import com.crypto.cryptoview.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val validateUpbitApi: ValidateUpbitApi
) : AuthRepository {

    override suspend fun validateAndSaveUpbit(
        accessKey: String,
        secretKey: String
    ): ValidateUpbitResponse {
        return withContext(Dispatchers.IO) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException("로그인이 필요합니다")

            val idToken = firebaseUser.getIdToken(false).await().token
                ?: throw IllegalStateException("Firebase 토큰 획득 실패")

            validateUpbitApi.validateCredentials(
                token = "Bearer $idToken",
                request = ValidateUpbitRequest(accessKey, secretKey)
            )
        }
    }
}
