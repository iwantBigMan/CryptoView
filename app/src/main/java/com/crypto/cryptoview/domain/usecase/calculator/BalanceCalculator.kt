package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.ExchangeData
import com.crypto.cryptoview.domain.model.HoldingData

/**
 * 잔고 계산기 인터페이스
 * @param BALANCE 거래소별 잔고 데이터 타입 (UpbitAccountBalance, ForeignBalance 등)
 * @param TICKER 거래소별 시세 데이터 타입 (List<UpbitMarketTicker>, Map<String, Double> 등)
 */
interface BalanceCalculator<BALANCE, TICKER> {

    /**
     * 계산 결과 데이터 클래스
     * @property totalValue 총 자산 가치 (KRW)
     * @property holdings 보유 자산 목록
     * @property exchangeData 거래소별 자산 데이터
     */
    data class CalculationResult(
        val totalValue: Double,
        val holdings: List<HoldingData>,
        val exchangeData: ExchangeData
    )

    /**
     * 잔고 계산
     * @param balances 잔고 목록
     * @param tickers 시세 데이터
     * @param usdtKrwRate USDT/KRW 환율 (해외 거래소용)
     * @return 계산 결과
     */
    fun calculate(balances: List<BALANCE>, tickers: TICKER, usdtKrwRate: Double = 0.0): CalculationResult
}