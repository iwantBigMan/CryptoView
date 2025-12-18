package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.upbit.gateio.GateFuturesAccountDto
import com.crypto.cryptoview.data.remote.dto.upbit.gateio.GateFuturesPositionDto
import com.crypto.cryptoview.data.remote.dto.upbit.gateio.GateSpotBalanceDto
import com.crypto.cryptoview.data.remote.dto.upbit.gateio.GateTickerDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GateSpotApi {

    // 현물 자산
    @GET("spot/accounts")
    suspend fun getSpotBalances(): List<GateSpotBalanceDto>

    // 현물 ticker (단일)
    @GET("spot/tickers")
    suspend fun getSpotTickers(
        @Query("currency_pair") currencyPair: String
    ): List<GateTickerDto>
}

interface GateFuturesApi {

    // 선물 계정 (USDT 기준)
    @GET("futures/usdt/accounts")
    suspend fun getFuturesAccount(): GateFuturesAccountDto

    // 선물 포지션
    @GET("futures/usdt/positions")
    suspend fun getPositions(): List<GateFuturesPositionDto>

    // 선물 ticker
    @GET("futures/usdt/tickers")
    suspend fun getFuturesTickers(
        @Query("contract") contract: String
    ): List<GateTickerDto>
}

