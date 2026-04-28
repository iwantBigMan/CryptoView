package com.crypto.cryptoview.domain.model.asset

import com.crypto.cryptoview.domain.model.exchange.ExchangeType

/**
 * 여러 거래소에서 보유한 동일 코인을 통합한 모델
 * 홈/홀딩 화면에서 코인별 총 보유량을 표시할 때 사용
 *
 * @property normalizedSymbol 정규화된 심볼 (예: BTC, ETH) - 거래소 무관
 * @property name 코인 이름 (예: Bitcoin, Ethereum)
 * @property totalBalance 모든 거래소 합산 보유량
 * @property totalValue 모든 거래소 합산 평가금액 (KRW)
 * @property totalChange 총 손익금액
 * @property totalChangePercent 총 손익률
 * @property holdings 거래소별 상세 보유 정보 (디테일 화면에서 사용)
 */
data class AggregatedHolding(
    val normalizedSymbol: String,
    val name: String,
    val totalBalance: Double,
    val totalValue: Double,
    val totalChange: Double,
    val totalChangePercent: Double,
    val holdings: List<HoldingData>
) {
    /**
     * 보유 중인 거래소 목록
     */
    val exchanges: List<ExchangeType>
        get() = holdings.map { it.exchange }.distinct()

    /**
     * 특정 거래소의 보유 정보 조회
     */
    fun getHoldingByExchange(exchange: ExchangeType): HoldingData? =
        holdings.find { it.exchange == exchange }

    /**
     * 여러 거래소에 보유 중인지 확인
     */
    val isMultiExchange: Boolean
        get() = holdings.size > 1
}
