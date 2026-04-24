package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.ExchangeData
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import javax.inject.Inject

/**
 * 업비트 잔고 계산기 (KRW 기준)
 * - 타입 안전한 제네릭 구현
 * - Any 캐스팅 없이 직접 타입 사용
 */
class UpbitBalanceCalculator @Inject constructor() :
    BaseBalanceCalculator<UpbitAccountBalance, List<UpbitMarketTicker>>() {

    override val baseCurrency: String = "KRW"
    override val exchangeType: ExchangeType = ExchangeType.UPBIT

    /**
     * 업비트 잔고 계산
     * @param balances 업비트 계좌 잔고 목록
     * @param tickers 업비트 시세 목록
     * @param usdtKrwRate 사용하지 않음 (국내 거래소)
     * @return 계산 결과
     */
    override fun calculate(
        balances: List<UpbitAccountBalance>,
        tickers: List<UpbitMarketTicker>,
        usdtKrwRate: Double
    ): BalanceCalculator.CalculationResult {
        if (balances.isEmpty()) return emptyResult()

        // KRW 잔고 추출
        val krwBalance = balances.find { it.currency == baseCurrency }?.balance ?: 0.0

        // 암호화폐 현재가치 합산
        val cryptoCurrentValue = balances
            .filter { it.currency != baseCurrency }
            .sumOf { balance ->
                val currentPrice = getCurrentPrice(balance.currency, tickers) ?: 0.0
                balance.balance * currentPrice
            }

        val totalValue = krwBalance + cryptoCurrentValue
        val holdings = calculateHoldings(balances, tickers)

        return BalanceCalculator.CalculationResult(
            totalValue = totalValue,
            holdings = holdings,
            exchangeData = ExchangeData(exchangeType, totalValue)
        )
    }

    /**
     * 보유 자산 목록 계산
     * @param balances 잔고 목록
     * @param tickers 시세 목록
     * @return 보유 자산 목록 (1원 이상, 가치 내림차순)
     */
    private fun calculateHoldings(
        balances: List<UpbitAccountBalance>,
        tickers: List<UpbitMarketTicker>
    ): List<HoldingData> {
        return balances
            .filter { it.currency != baseCurrency && it.balance > 0 }
            .mapNotNull { balance ->
                val currentPrice = getCurrentPrice(balance.currency, tickers) ?: return@mapNotNull null
                createHoldingData(
                    symbol = balance.currency,
                    amount = balance.balance,
                    avgBuyPrice = balance.avgBuyPrice,
                    currentPrice = currentPrice,
                    exchange = exchangeType
                )
            }
            .filter { it.totalValue >= 1.0 }
            .sortedByDescending { it.totalValue }
    }

    /**
     * 현재가 조회
     * @param currency 통화 코드
     * @param tickers 시세 목록
     * @return 현재가 (없으면 null)
     */
    private fun getCurrentPrice(currency: String, tickers: List<UpbitMarketTicker>): Double? {
        return tickers.find { it.market == "KRW-$currency" }?.tradePrice
    }
}