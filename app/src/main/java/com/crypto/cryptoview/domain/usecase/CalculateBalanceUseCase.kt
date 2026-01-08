package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.model.ForeignBalance
import com.crypto.cryptoview.domain.model.GateSpotBalance
import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker
import com.crypto.cryptoview.domain.model.toForeignBalance
import com.crypto.cryptoview.domain.usecase.calculator.BalanceCalculator
import com.crypto.cryptoview.domain.usecase.calculator.BalanceCalculatorFactory
import com.crypto.cryptoview.domain.usecase.calculator.ExchangeRateProvider
import javax.inject.Inject

/**
 * 잔고 계산 유스케이스
 * - 여러 거래소 잔고 계산 조율
 * - 환율 조회 위임
 * - Calculator 호출 및 결과 통합
 */
class CalculateBalanceUseCase @Inject constructor(
    private val calculatorFactory: BalanceCalculatorFactory,
    private val exchangeRateProvider: ExchangeRateProvider
) {

    /**
     * 업비트 잔고 계산
     * @param balances 업비트 계좌 잔고 목록
     * @param tickers 업비트 시세 목록
     * @return 계산 결과
     */
    fun calculateUpbit(
        balances: List<UpbitAccountBalance>,
        tickers: List<UpbitMarketTicker>
    ): BalanceCalculator.CalculationResult {
        return calculatorFactory.getUpbitCalculator().calculate(balances, tickers)
    }

    /**
     * 해외 거래소 잔고 계산 (KRW 환산)
     * @param balances 해외 거래소 잔고 목록
     * @param tickers 시세 맵
     * @param usdtKrwRate USDT/KRW 환율
     * @param exchangeType 거래소 타입
     * @return 계산 결과
     */
    fun calculateForeign(
        balances: List<ForeignBalance>,
        tickers: Map<String, Double>,
        usdtKrwRate: Double,
        exchangeType: ExchangeType
    ): BalanceCalculator.CalculationResult {
        return calculatorFactory.getForeignCalculator(exchangeType)
            .calculate(balances, tickers, usdtKrwRate)
    }

    /**
     * USDT/KRW 시세 조회
     * @param upbitTickers 업비트 시세 목록
     * @return USDT/KRW 환율
     */
    fun getUsdtKrwRate(upbitTickers: List<UpbitMarketTicker>): Double {
        return exchangeRateProvider.getUsdtKrwRate(upbitTickers)
    }

    /**
     * 전체 거래소 잔고 통합 계산
     * @return 통합 계산 결과
     */
fun calculateAll(
    upbitBalances: List<UpbitAccountBalance>,
    upbitTickers: List<UpbitMarketTicker>,
    gateioBalances: List<GateSpotBalance>,
    gateioTickers: List<GateSpotTicker>
): TotalBalanceResult {
    val usdtKrwRate = getUsdtKrwRate(upbitTickers)
    val results = mutableListOf<BalanceCalculator.CalculationResult>()

    // 업비트 계산
    results.add(calculateUpbit(upbitBalances, upbitTickers))

    // 해외 거래소 계산 (잔고가 있는 경우만)
//    if (binanceBalances.isNotEmpty()) {
//        results.add(calculateForeign(binanceBalances, binanceTickers, usdtKrwRate, ExchangeType.BINANCE))
//    }
//    if (bybitBalances.isNotEmpty()) {
//        results.add(calculateForeign(bybitBalances, bybitTickers, usdtKrwRate, ExchangeType.BYBIT))
//    }
if (gateioBalances.isNotEmpty()) {
    val foreignBalances = gateioBalances.map { it.toForeignBalance() }
    val tickerMap = gateioTickers.associate { ticker ->
        // XRP_USDT -> XRPUSDT 변환
        ticker.symbol.replace("_", "") to ticker.lastPrice
    }
    results.add(calculateForeign(foreignBalances, tickerMap, usdtKrwRate, ExchangeType.GATEIO))
}

    return TotalBalanceResult(
        totalValue = results.sumOf { it.totalValue },
        results = results,
        usdtKrwRate = usdtKrwRate
    )
}

    /**
     * 통합 계산 결과
     * @property totalValue 전체 자산 가치 (KRW)
     * @property results 거래소별 계산 결과 목록
     * @property usdtKrwRate 사용된 USDT/KRW 환율
     */
    data class TotalBalanceResult(
        val totalValue: Double,
        val results: List<BalanceCalculator.CalculationResult>,
        val usdtKrwRate: Double
    )
}