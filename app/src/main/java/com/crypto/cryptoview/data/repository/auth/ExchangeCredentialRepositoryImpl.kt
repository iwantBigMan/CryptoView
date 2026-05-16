package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.local.CredentialsManager
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.repository.ExchangeCredentialRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeCredentialRepositoryImpl @Inject constructor(
    private val credentialsManager: CredentialsManager
) : ExchangeCredentialRepository {

    override suspend fun getSavedExchanges(): List<ExchangeType> {
        val savedExchanges = mutableListOf<ExchangeType>()

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
        // 백엔드 credential 구조에서는 클라이언트 메모리 credential 캐시를 유지하지 않습니다.
    }

    override suspend fun hasAnyCredentials(): Boolean {
        return credentialsManager.hasUpbitLinked()
            || credentialsManager.hasGateIoLinked()
    }
}
