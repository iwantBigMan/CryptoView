package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.DeleteUpbitCredentialResponse
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioInsightRequestDto
import com.crypto.cryptoview.data.remote.dto.ai.AiPortfolioInsightResponseDto
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoCredentialDeleteResponse
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoSpotAveragePriceRequest
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoSpotAveragePriceResponse
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoValidateAndSaveRequest
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoValidateAndSaveResponse
import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto
import com.crypto.cryptoview.data.remote.dto.upbit.UpbitAccountBalanceDto
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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

interface DeleteUpbitCredentials {
    @DELETE("api/exchange/upbit/credential")
    suspend fun deleteUpbitCredential(
        @Header("Authorization") token: String
    ): DeleteUpbitCredentialResponse
}

interface ValidateAndSaveGateIo {
    @POST("api/exchange/gateio/validate-and-save")
    suspend fun validateAndSaveCredential(
        @Body request: GateIoValidateAndSaveRequest
    ): GateIoValidateAndSaveResponse
}

interface FetchGateIoAccounts {
    @GET("api/exchange/gateio/accounts")
    suspend fun fetchAccounts(): List<GateSpotBalanceDto>
}

interface DeleteGateIoCredential {
    @DELETE("api/exchange/gateio/credential")
    suspend fun deleteGateIoCredential(
        @Header("Authorization") token: String
    ): GateIoCredentialDeleteResponse
}

interface FetchGateIoSpotAveragePrice {
    @POST("api/exchange/gateio/spot-average-price")
    suspend fun fetchSpotAveragePrice(
        @Body request: GateIoSpotAveragePriceRequest
    ): GateIoSpotAveragePriceResponse
}

interface AiPortfolioInsightApi {
    @POST("api/ai/portfolio-insight")
    suspend fun generatePortfolioInsight(
        @Body request: AiPortfolioInsightRequestDto
    ): AiPortfolioInsightResponseDto
}
