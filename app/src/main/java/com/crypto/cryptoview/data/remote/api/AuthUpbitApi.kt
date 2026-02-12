package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.UpbitAccountBalanceDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthUpbitApi {
    @GET("v1/accounts")
    suspend fun getAccounts(@Header("Authorization") token: String): List<UpbitAccountBalanceDto>
}
