package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.ExchangeData
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.model.HoldingData

/**
 * 잔고 계산기 기본 구현 (공통 로직)
 * @param BALANCE 거래소별 잔고 데이터 타입
 * @param TICKER 거래소별 시세 데이터 타입
 */
abstract class BaseBalanceCalculator<BALANCE, TICKER> : BalanceCalculator<BALANCE, TICKER> {

    /** 기준 통화 (KRW, USDT 등) */
    abstract val baseCurrency: String

    /** 거래소 타입 */
    abstract val exchangeType: ExchangeType

    /**
     * 보유 자산 데이터 생성 (공통 유틸)
     * @param symbol 자산 심볼
     * @param amount 보유 수량
     * @param avgBuyPrice 평균 매수가
     * @param currentPrice 현재가
     * @param exchange 거래소 타입
     * @return 보유 자산 데이터
     */
    protected fun createHoldingData(
        symbol: String,
        amount: Double,
        avgBuyPrice: Double,
        currentPrice: Double,
        exchange: ExchangeType
    ): HoldingData {
        val totalValue = amount * currentPrice
        val buyValue = amount * avgBuyPrice
        val change = totalValue - buyValue
        val changePercent = if (buyValue > 0) (change / buyValue) * 100 else 0.0

        return HoldingData(
            symbol = symbol,
            name = symbol,
            currentPrice = currentPrice,
            balance = amount,
            totalValue = totalValue,
            change = change,
            changePercent = changePercent,
            exchange = exchange
        )
    }

    /**
     * 빈 결과 반환 (공통 유틸)
     * @return 빈 계산 결과
     */
    protected fun emptyResult(): BalanceCalculator.CalculationResult {
        return BalanceCalculator.CalculationResult(
            totalValue = 0.0,
            holdings = emptyList(),
            exchangeData = ExchangeData(exchangeType, 0.0)
        )
    }
}