package com.crypto.cryptoview.domain.usecase.gate

import com.crypto.cryptoview.domain.model.gate.GateSpotTicker
import com.crypto.cryptoview.domain.repository.GateSpotRepository
import javax.inject.Inject

class GetGateSpotTickersUseCase @Inject constructor(
    private val gateSpotRepository: GateSpotRepository
) {
    suspend operator fun invoke(currencyPair: String): Result<List<GateSpotTicker>> {
        return gateSpotRepository.getSpotTickers(currencyPair)
    }
}