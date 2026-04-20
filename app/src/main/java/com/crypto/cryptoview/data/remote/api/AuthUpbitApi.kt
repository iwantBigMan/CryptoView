package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 백엔드 서버를 통한 업비트 키 검증 API
 */
interface ValidateUpbitApi {
    @POST("/upbit/validate")
    suspend fun validateCredentials(
        @Header("Authorization") token: String,
        @Body request: ValidateUpbitRequest
    ): ValidateUpbitResponse
}

interface validateAndSaveUpbit {
    @POST("/api/exchange/upbit/validate-and-save")
    suspend fun validateAndSaveCredentials(
        @Header("Authorization") token: String,
        @Body request: ValidateUpbitRequest
    ): ValidateUpbitResponse
}