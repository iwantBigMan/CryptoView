package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.upbit.UpbitAccountBalance
import com.crypto.cryptoview.domain.model.upbit.UpbitTickerAll
import com.crypto.cryptoview.domain.model.upbit.UpbitMarketTicker

interface UpbitAssetRepository {
    suspend fun getAccountBalances(): Result<List<UpbitAccountBalance>>
}

interface UbbitMTickerRepository{
    suspend fun getMarketTickers(currencies: List<String>): Result<List<UpbitMarketTicker>>
}

interface UpbitTickerAllRepository{
    suspend fun getAllMarkets(): Result<List<UpbitTickerAll>>
}