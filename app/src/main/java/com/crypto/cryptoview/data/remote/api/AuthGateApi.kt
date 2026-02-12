package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthGateApi {
    @GET("/api/v4/spot/accounts")
    suspend fun getSpotAccounts(
        @Header("KEY") apiKey: String,
        @Header("Timestamp") timestamp: String,
        @Header("SIGN") sign: String
    ): List<GateSpotBalanceDto>
}
