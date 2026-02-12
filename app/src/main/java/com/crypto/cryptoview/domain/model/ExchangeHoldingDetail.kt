package com.crypto.cryptoview.domain.model

/**
 * 거래소별 보유 상세 정보 (도메인 모델)
 * @property exchange 거래소 타입
 * @property symbol 코인 심볼
 * @property quantity 보유 수량
 * @property avgBuyPrice 평균 매수가 (null이면 평단 정보 없음)
 * @property currentPrice 현재가 (거래소 마켓 기준)
 * @property currencyUnit 가격 단위 ("KRW" or "USDT")
 * @property valueKrw 원화 환산 가치
 * @property profitLoss 손익 (원화)
 * @property profitLossPercent 손익률 (%)
 */
data class ExchangeHoldingDetail(
    val exchange: ExchangeType,
    val symbol: String,
    val quantity: Double,
    val avgBuyPrice: Double?,
    val currentPrice: Double,
    val currencyUnit: CurrencyUnit,
    val valueKrw: Double,
    val profitLoss: Double?,
    val profitLossPercent: Double?
)

/**
 * 가격 단위
 */
enum class CurrencyUnit(val symbol: String) {
    KRW("KRW"),
    USDT("USDT")
}
