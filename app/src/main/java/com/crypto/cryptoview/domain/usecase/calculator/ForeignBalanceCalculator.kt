package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.ExchangeData
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.model.ForeignBalance
import javax.inject.Inject


/**
 * 해외 거래소 잔고 계산기 (USDT → KRW 환산)
 * - 바이낸스, 바이빗, 게이트아이오 공용
 * - 타입 안전한 제네릭 구현
 */
class ForeignBalanceCalculator @Inject constructor() :
    BaseBalanceCalculator<ForeignBalance, Map<String, Double>>() {

    override val baseCurrency: String = "USDT"

    /** 현재 계산 대상 거래소 (외부에서 설정) */
    override var exchangeType: ExchangeType = ExchangeType.BINANCE

    /**
     * 해외 거래소 잔고 계산 (KRW 환산)
     * @param balances 해외 거래소 잔고 목록
     * @param tickers 시세 맵 (symbol -> USDT 가격)
     * @param usdtKrwRate 업비트 USDT/KRW 시세
     * @return 계산 결과 (KRW 기준)
     */
    override fun calculate(
        balances: List<ForeignBalance>,
        tickers: Map<String, Double>,
        usdtKrwRate: Double
    ): BalanceCalculator.CalculationResult {
        if (balances.isEmpty()) return emptyResult()

        val holdings = balances
            .filter { it.free > 0 }
            .map { balance ->
                // USDT 가격 조회 (USDT는 1.0)
                val priceUsdt = tickers["${balance.asset}USDT"]

                // KRW 환산
                val priceKrw = priceUsdt?.times(usdtKrwRate)
                val avgBuyPriceKrw = balance.avgBuyPriceUsdt * usdtKrwRate

                createHoldingData(
                    symbol = balance.asset,
                    amount = balance.free,
                    avgBuyPrice = avgBuyPriceKrw,
                    currentPrice = priceKrw,
                    exchange = exchangeType
                )
            }
            .sortedByDescending { it.totalValue }

        val totalValue = holdings.sumOf { it.totalValue }

        return BalanceCalculator.CalculationResult(
            totalValue = totalValue,
            holdings = holdings,
            exchangeData = ExchangeData(exchangeType, totalValue)
        )
    }
}