package com.crypto.cryptoview.domain.model.exchange

data class ExchangeCredentials(
    val binanceApiKey: String = "",
    val binanceSecretKey: String = "",
    val bybitApiKey: String = "",
    val bybitSecretKey: String = ""
) {
    fun hasBinanceCredentials() = binanceApiKey.isNotEmpty() && binanceSecretKey.isNotEmpty()
    fun hasBybitCredentials() = bybitApiKey.isNotEmpty() && bybitSecretKey.isNotEmpty()

    fun hasAnyCredentials() = hasBinanceCredentials() || hasBybitCredentials()
}
