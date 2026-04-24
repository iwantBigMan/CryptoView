package com.crypto.cryptoview.domain.usecase.gate

import com.crypto.cryptoview.domain.model.gate.GateFuturesAccount
import com.crypto.cryptoview.domain.repository.GateFuturesRepository
import javax.inject.Inject

class GetGateFuturesAccountUseCase @Inject constructor(
    private val gateFuturesRepository: GateFuturesRepository
) {
    suspend operator fun invoke(): Result<GateFuturesAccount> {
        return gateFuturesRepository.getFuturesAccount()
    }
}