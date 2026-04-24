package com.crypto.cryptoview.domain.usecase.gate

import com.crypto.cryptoview.domain.model.gate.GateFuturesPosition
import com.crypto.cryptoview.domain.repository.GateFuturesRepository
import javax.inject.Inject

class GetGateFuturesPositionsUseCase @Inject constructor(
    private val gateFuturesRepository: GateFuturesRepository
) {
    suspend operator fun invoke(): Result<List<GateFuturesPosition>> {
        return gateFuturesRepository.getPositions()
    }
}