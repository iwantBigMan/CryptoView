package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse

/**
 * 인증 관련 리포지토리 (백엔드 API 키 검증)
 */
interface AuthRepository {
    /** 백엔드에 업비트 키 검증+저장 요청 (Firebase 토큰 자동 첨부) */
    suspend fun validateAndSaveUpbit(accessKey: String, secretKey: String): ValidateUpbitResponse

}