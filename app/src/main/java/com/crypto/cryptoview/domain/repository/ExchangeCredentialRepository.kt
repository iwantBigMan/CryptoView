package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.exchange.ExchangeType

interface ExchangeCredentialRepository {
    suspend fun getSavedExchanges(): List<ExchangeType>
    suspend fun markUpbitLinked()
    suspend fun markGateIoLinked()
    suspend fun clearCredentials(exchangeType: ExchangeType)
    suspend fun clearAllCredentials()
    fun clearCache()
    suspend fun hasAnyCredentials(): Boolean
}
