package com.crypto.cryptoview.domain.model.exchange

/**
 * 거래소 인증 정보
 */
data class ExchangeCredentials(
    val upbitApiKey: String = "",
    val upbitSecretKey: String = "",
    val gateioApiKey: String = "",
    val gateioSecretKey: String = "",
    val binanceApiKey: String = "",
    val binanceSecretKey: String = "",
    val bybitApiKey: String = "",
    val bybitSecretKey: String = ""
) {
    fun hasUpbitCredentials() = upbitApiKey.isNotEmpty() && upbitSecretKey.isNotEmpty()
    fun hasGateioCredentials() = gateioApiKey.isNotEmpty() && gateioSecretKey.isNotEmpty()
    fun hasBinanceCredentials() = binanceApiKey.isNotEmpty() && binanceSecretKey.isNotEmpty()
    fun hasBybitCredentials() = bybitApiKey.isNotEmpty() && bybitSecretKey.isNotEmpty()

    /**
     * 필수 인증 확인 (업비트 필수 - USDT/KRW 환율 조회 필요)
     */
    fun hasRequiredCredentials() = hasUpbitCredentials()

    /**
     * 선택적 인증 확인 (해외 거래소)
     */
    fun hasAnyCredentials() = hasUpbitCredentials() || hasGateioCredentials()
        || hasBinanceCredentials() || hasBybitCredentials()
}
