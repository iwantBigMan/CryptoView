package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.UpbitAccountBalanceDto
import com.crypto.cryptoview.data.remote.dto.upbit.UpbitMarketTickerDto
import com.crypto.cryptoview.data.remote.dto.upbit.UpbitTickerAllDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UpbitApi {
    @GET("v1/accounts")
    suspend fun getUpbitAccountBalances(): List<UpbitAccountBalanceDto>
}

interface UpbitMarketApi {
    @GET("v1/ticker")
    suspend fun getTickers(
        @Query("markets") markets: String
    ): List<UpbitMarketTickerDto>
}


interface UpbitTickerAllApi {
    @GET("v1/ticker/all")
    suspend fun getAllTickers(
        @Query("quoteCurrencies") quoteCurrencies: String
    ): List<UpbitTickerAllDto>
}