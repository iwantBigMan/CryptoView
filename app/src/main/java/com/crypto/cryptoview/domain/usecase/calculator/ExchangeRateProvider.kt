package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import javax.inject.Inject

/**
 * 환율 제공자
 * - 업비트 USDT/KRW 시세 조회 (견고하게 여러 포맷 처리)
 * - CalculateBalanceUseCase 또는 다른 도메인 레이어에서 호출
 */
class ExchangeRateProvider @Inject constructor() {

    /**
     * USDT/KRW 환율 조회
     * - 업비트 티커 목록에서 가능한 모든 포맷을 시도하여 USDT↔KRW 페어를 찾음
     * @param upbitTickers 업비트 시세 목록
     * @return USDT/KRW 시세 (기본값 1300.0)
     */
    fun getUsdtKrwRate(upbitTickers: List<UpbitMarketTicker>): Double {
        // 우선 정확한 매칭을 시도
        val exactUsdtKrw = upbitTickers.firstOrNull { it.market.equals("USDT-KRW", ignoreCase = true) }
        if (exactUsdtKrw != null) {
            android.util.Log.d("ExchangeRateProvider", "USDT/KRW 환율(USDT-KRW): ${exactUsdtKrw.tradePrice}")
            return exactUsdtKrw.tradePrice
        }

        val exactKrwUsdt = upbitTickers.firstOrNull { it.market.equals("KRW-USDT", ignoreCase = true) }
        if (exactKrwUsdt != null) {
            android.util.Log.d("ExchangeRateProvider", "USDT/KRW 환율(KRW-USDT): ${exactKrwUsdt.tradePrice}")
            return exactKrwUsdt.tradePrice
        }

        // 그 외에 market 문자열 내에 둘 다 포함되어 있는 항목을 찾음 (순서 무관)
        val containsBoth = upbitTickers.firstOrNull {
            val m = it.market.uppercase()
            m.contains("USDT") && m.contains("KRW")
        }
        if (containsBoth != null) {
            android.util.Log.d("ExchangeRateProvider", "USDT/KRW 환율(contains): ${containsBoth.tradePrice} (market=${containsBoth.market})")
            return containsBoth.tradePrice
        }

        // 일부 API 응답에서는 구분자가 다를 수 있으니 구분자 통일 후 재검색
        val normalized = upbitTickers.firstOrNull {
            val m = it.market.replace('_', '-').replace('/', '-').uppercase()
            m == "USDT-KRW" || m == "KRW-USDT" || m == "USDTKRW" || m == "KRWUSDT"
        }
        if (normalized != null) {
            android.util.Log.d("ExchangeRateProvider", "USDT/KRW 환율(normalized): ${normalized.tradePrice} (market=${normalized.market})")
            return normalized.tradePrice
        }

        // 찾지 못하면 기본값 사용 (로그 남김)
        android.util.Log.w("ExchangeRateProvider", "KRW-USDT 시세를 찾을 수 없습니다. 기본값 1300.0 사용")
        return 1300.0
    }
}