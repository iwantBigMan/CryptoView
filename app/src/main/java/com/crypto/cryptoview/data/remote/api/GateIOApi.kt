package com.crypto.cryptoview.data.remote.api

import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotBalanceDto
import com.crypto.cryptoview.data.remote.dto.gateio.GateSpotTickerDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GateSpotApi {

    // 현물 ticker (단일)
    @GET("spot/tickers")
    suspend fun getSpotTickers(
        @Query("currency_pair") currencyPair: String?
    ): List<GateSpotTickerDto>
}
