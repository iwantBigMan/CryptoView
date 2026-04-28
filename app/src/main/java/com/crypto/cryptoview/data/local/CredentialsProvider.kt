package com.crypto.cryptoview.data.local

import com.crypto.cryptoview.domain.model.exchange.ExchangeCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicReference

/**
 * Runtime provider that keeps the latest decrypted ExchangeCredentials in memory.
 * It subscribes to CredentialsManager.credentials Flow and updates an AtomicReference.
 */
class CredentialsProvider(
    credentialsManager: CredentialsManager,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val ref = AtomicReference<ExchangeCredentials>(ExchangeCredentials())

    init {
        credentialsManager.credentials
            .onEach { ref.set(it) }
            .launchIn(scope)
    }

    fun get(): ExchangeCredentials = ref.get()

    /**
     * 메모리 캐시를 초기화합니다 (로그아웃 시 사용)
     */
    fun clear() {
        ref.set(ExchangeCredentials())
    }
}
