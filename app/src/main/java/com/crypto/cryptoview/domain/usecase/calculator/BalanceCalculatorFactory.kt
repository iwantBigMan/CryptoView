package com.crypto.cryptoview.domain.usecase.calculator

import com.crypto.cryptoview.domain.model.ExchangeType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 잔고 계산기 팩토리
 * - 거래소 타입별 계산기 제공
 * - 단순 getter 역할만 수행
 */
@Singleton
class BalanceCalculatorFactory @Inject constructor(
    private val upbitCalculator: UpbitBalanceCalculator,
    private val foreignCalculator: ForeignBalanceCalculator
) {

    /**
     * 업비트 계산기 반환
     * @return 업비트 잔고 계산기
     */
    fun getUpbitCalculator(): UpbitBalanceCalculator = upbitCalculator

    /**
     * 해외 거래소 계산기 반환
     * @param exchangeType 거래소 타입 (BINANCE, BYBIT, GATEIO)
     * @return 해외 거래소 잔고 계산기 (타입 설정됨)
     */
    fun getForeignCalculator(exchangeType: ExchangeType): ForeignBalanceCalculator {
        return foreignCalculator.apply { this.exchangeType = exchangeType }
    }
}