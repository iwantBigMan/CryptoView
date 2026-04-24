package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.UpbitAccountBalanceDto
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 백엔드 서버를 통한 업비트 키 검증 API
 * Authorization 헤더는 FirebaseAuthInterceptor 가 자동 주입
 */
interface ValidateAndSaveUpbit {
    @POST("/api/exchange/upbit/validate-and-save")
    suspend fun validateAndSaveCredentials(
        @Body request: ValidateUpbitRequest
    ): ValidateUpbitResponse
}

interface FetchUpbitAssets {
    @GET("api/exchange/upbit/accounts")
    suspend fun fetchAssets(): List<UpbitAccountBalanceDto>
}