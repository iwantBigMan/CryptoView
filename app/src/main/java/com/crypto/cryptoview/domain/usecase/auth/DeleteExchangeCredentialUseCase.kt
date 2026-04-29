package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.data.remote.dto.upbit.DeleteUpbitCredentialResponse
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteExchangeCredentialUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(exchangeType: ExchangeType): DeleteUpbitCredentialResponse {
        return when (exchangeType) {
            ExchangeType.UPBIT -> authRepository.deleteUpbitCredential()
            else -> throw UnsupportedOperationException("${exchangeType.displayName} 삭제 미지원")
        }
    }
}

