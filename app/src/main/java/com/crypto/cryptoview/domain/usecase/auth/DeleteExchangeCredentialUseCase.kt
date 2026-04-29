package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.model.auth.CredentialDeletionResult
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteExchangeCredentialUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(exchangeType: ExchangeType): CredentialDeletionResult {
        return when (exchangeType) {
            ExchangeType.UPBIT -> authRepository.deleteUpbitCredential()
            else -> throw UnsupportedOperationException("${exchangeType.displayName} deletion is not supported")
        }
    }
}
