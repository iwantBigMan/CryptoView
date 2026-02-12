package com.crypto.cryptoview.domain.repository

/**
 * 인증 관련 리포지토리(외부 API 키 검증)
 */
interface AuthRepository {
    suspend fun validateUpbit(apiKey: String, secretKey: String): Boolean
    suspend fun validateGate(apiKey: String, secretKey: String): Boolean
}
