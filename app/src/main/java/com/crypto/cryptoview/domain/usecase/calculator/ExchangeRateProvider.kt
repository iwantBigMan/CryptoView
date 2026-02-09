package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import com.crypto.cryptoview.domain.model.UpbitTickerAll
import javax.inject.Inject

/**
 * 환율 제공자
 * - 업비트 USDT/KRW 시세 조회
 * - Calculator에서 사용
 */
class ExchangeRateProvider @Inject constructor() {

    /**
     * USDT/KRW 환율 조회
     * @param upbitTickers 업비트 시세 목록
     * @return USDT/KRW 시세 (기본값 1300.0)
     */
    fun getUsdtKrwRate(upbitTickers: List<UpbitMarketTicker>): Double {
        // ✅ 업비트 USDT 마켓은 "USDT-KRW"
        val usdtTicker = upbitTickers.find { it.market == "KRW-USDT" }

        return usdtTicker?.tradePrice?.also { price ->
            android.util.Log.d("ExchangeRateProvider", "USDT/KRW 환율: $price")
        } ?: run {
            android.util.Log.w("ExchangeRateProvider", "KRW-USDT 시세를 찾을 수 없습니다. 기본값 1300.0 사용")
            1300.0
        }
    }
}