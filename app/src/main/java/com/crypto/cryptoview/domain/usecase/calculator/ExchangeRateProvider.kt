package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 환율 제공자 (USDT/KRW)
 */
@Singleton
class ExchangeRateProvider @Inject constructor() {

    private var cachedRate: Double = DEFAULT_USDT_KRW_RATE

    fun getUsdtKrwRate(upbitTickers: List<UpbitMarketTicker>): Double {
        val rate = upbitTickers.find { it.market == "KRW-USDT" }?.tradePrice
            ?: DEFAULT_USDT_KRW_RATE
        cachedRate = rate
        return rate
    }

    fun getCachedRate(): Double = cachedRate

    companion object {
        const val DEFAULT_USDT_KRW_RATE = 1380.0
    }
}