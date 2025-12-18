package com.crypto.cryptoview.domain.usecase.gate

import com.crypto.cryptoview.domain.model.gate.GateFuturesTicker
import com.crypto.cryptoview.domain.repository.GateFuturesRepository
import javax.inject.Inject

class GetGateFuturesTickersUseCase @Inject constructor(
    private val gateFuturesRepository: GateFuturesRepository
) {
    suspend operator fun invoke(): Result<List<GateFuturesTicker>> {
        return gateFuturesRepository.getFuturesTickers()
    }
}