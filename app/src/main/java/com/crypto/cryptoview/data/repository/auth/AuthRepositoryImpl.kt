package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.auth.FirebaseTokenProvider
import com.crypto.cryptoview.data.remote.api.DeleteGateIoCredential
import com.crypto.cryptoview.data.remote.api.DeleteUpbitCredentials
import com.crypto.cryptoview.data.remote.api.ValidateAndSaveGateIo
import com.crypto.cryptoview.data.remote.api.ValidateAndSaveUpbit
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoValidateAndSaveRequest
import com.crypto.cryptoview.data.remote.mapper.toDomain
import com.crypto.cryptoview.data.remote.mapper.toGateIoCredentialDeleteMessage
import com.crypto.cryptoview.data.remote.mapper.toGateIoCredentialSaveMessage
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.domain.model.auth.CredentialDeletionResult
import com.crypto.cryptoview.domain.model.auth.CredentialValidationResult
import com.crypto.cryptoview.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val validateAndSaveUpbit: ValidateAndSaveUpbit,
    private val deleteUpbitCredentials: DeleteUpbitCredentials,
    private val validateAndSaveGateIo: ValidateAndSaveGateIo,
    private val deleteGateIoCredential: DeleteGateIoCredential,
    private val tokenProvider: FirebaseTokenProvider
) : AuthRepository {

    override suspend fun validateAndSaveUpbit(
        accessKey: String,
        secretKey: String
    ): CredentialValidationResult {
        return withContext(Dispatchers.IO) {
            validateAndSaveUpbit.validateAndSaveCredentials(
                request = ValidateUpbitRequest(accessKey, secretKey)
            ).toDomain()
        }
    }

    override suspend fun validateAndSaveGateIo(
        accessKey: String,
        secretKey: String
    ): CredentialValidationResult {
        return withContext(Dispatchers.IO) {
            runCatching {
                validateAndSaveGateIo.validateAndSaveCredential(
                    request = GateIoValidateAndSaveRequest(accessKey, secretKey)
                ).toDomain()
            }.getOrElse { throw Exception(it.toGateIoCredentialSaveMessage(), it) }
        }
    }

    override suspend fun deleteUpbitCredential(): CredentialDeletionResult {
        return withContext(Dispatchers.IO) {
            val token = tokenProvider.getIdToken() ?: throw Exception("Firebase 토큰 없음")
            deleteUpbitCredentials.deleteUpbitCredential("Bearer $token").toDomain()
        }
    }

    override suspend fun deleteGateIoCredential(): CredentialDeletionResult {
        return withContext(Dispatchers.IO) {
            runCatching {
                val token = tokenProvider.getIdToken() ?: throw Exception("Firebase token is missing")
                deleteGateIoCredential.deleteGateIoCredential("Bearer $token").toDomain()
            }.getOrElse { throw Exception(it.toGateIoCredentialDeleteMessage(), it) }
        }
    }
}
