package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.UpbitMarketTickerDto
import com.crypto.cryptoview.data.remote.dto.upbit.UpbitTickerAllDto
import retrofit2.http.GET
import retrofit2.http.Query

// UpbitApi (v1/accounts 직접 호출) 제거 - 자산 조회는 백엔드 프록시(FetchUpbitAssets)로 대체

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