package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.local.CredentialsManager
import com.crypto.cryptoview.data.local.CredentialsProvider
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.repository.ExchangeCredentialRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeCredentialRepositoryImpl @Inject constructor(
    private val credentialsManager: CredentialsManager,
    private val credentialsProvider: CredentialsProvider
) : ExchangeCredentialRepository {

    override suspend fun getSavedExchanges(): List<ExchangeType> {
        val savedExchanges = mutableListOf<ExchangeType>()
        val credentials = credentialsManager.credentials.first()

        if (credentialsManager.hasUpbitLinked()) {
            savedExchanges.add(ExchangeType.UPBIT)
        }
        if (credentialsManager.hasGateIoLinked()) {
            savedExchanges.add(ExchangeType.GATEIO)
        }

        return savedExchanges
    }

    override suspend fun markUpbitLinked() {
        credentialsManager.markUpbitLinked()
    }

    override suspend fun markGateIoLinked() {
        credentialsManager.markGateIoLinked()
    }

    override suspend fun clearCredentials(exchangeType: ExchangeType) {
        when (exchangeType) {
            ExchangeType.UPBIT -> credentialsManager.clearUpbitLinkStatus()
            ExchangeType.GATEIO -> credentialsManager.clearGateIoLinkStatus()
            else -> Unit
        }
    }

    override suspend fun clearAllCredentials() {
        credentialsManager.clearAllCredentials()
    }

    override fun clearCache() {
        credentialsProvider.clear()
    }

    override suspend fun hasAnyCredentials(): Boolean {
        val localCredentials = credentialsManager.credentials.first()
        return credentialsManager.hasUpbitLinked()
            || credentialsManager.hasGateIoLinked()
            || localCredentials.hasAnyCredentials()
    }
}
