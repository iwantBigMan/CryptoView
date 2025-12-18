package com.crypto.cryptoview.domain.usecase.gate

import com.crypto.cryptoview.domain.model.GateSpotBalance
import com.crypto.cryptoview.domain.repository.GateSpotRepository
import javax.inject.Inject

class GetGateSpotBalancesUseCase @Inject constructor(
    private val gateSpotRepository: GateSpotRepository
) {
    suspend operator fun invoke(): Result<List<GateSpotBalance>> {
        return gateSpotRepository.getSpotBalances()
    }
}