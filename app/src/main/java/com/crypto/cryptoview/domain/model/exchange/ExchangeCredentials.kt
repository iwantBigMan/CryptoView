package com.crypto.cryptoview.domain.model.exchange

data class ExchangeCredentials(
    val gateioApiKey: String = "",
    val gateioSecretKey: String = "",
    val binanceApiKey: String = "",
    val binanceSecretKey: String = "",
    val bybitApiKey: String = "",
    val bybitSecretKey: String = ""
) {
    fun hasGateioCredentials() = gateioApiKey.isNotEmpty() && gateioSecretKey.isNotEmpty()
    fun hasBinanceCredentials() = binanceApiKey.isNotEmpty() && binanceSecretKey.isNotEmpty()
    fun hasBybitCredentials() = bybitApiKey.isNotEmpty() && bybitSecretKey.isNotEmpty()

    fun hasAnyCredentials() = hasGateioCredentials()
        || hasBinanceCredentials()
        || hasBybitCredentials()
}
