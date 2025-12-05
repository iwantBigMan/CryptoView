package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.UpbitAccountBalanceDto
import retrofit2.http.GET

interface UpbitApi {
    @GET("v1/accounts")
    suspend fun getUpbitAccountBalances(): List<UpbitAccountBalanceDto>
}