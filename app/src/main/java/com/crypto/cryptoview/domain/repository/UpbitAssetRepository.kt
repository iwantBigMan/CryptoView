package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.model.UpbitMarketTicker

interface UpbitAssetRepository {
    suspend fun getAccountBalances(): Result<List<UpbitAccountBalance>>
}

interface UbbitMTickerRepository{
    suspend fun getMarketTickers(): Result<List<UpbitMarketTicker>>
}