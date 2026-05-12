package com.crypto.cryptoview.domain.usecase.gate

import com.crypto.cryptoview.domain.model.gate.GateIoSpotAveragePrice
import com.crypto.cryptoview.domain.repository.GateSpotRepository
import javax.inject.Inject

class GetGateIoSpotAveragePriceUseCase @Inject constructor(
    private val gateSpotRepository: GateSpotRepository
) {
    suspend operator fun invoke(
        currencyPair: String,
        from: Long? = null,
        to: Long? = null,
        maxPages: Int? = null
    ): Result<GateIoSpotAveragePrice> {
        return gateSpotRepository.getSpotAveragePrice(
            currencyPair = currencyPair,
            from = from,
            to = to,
            maxPages = maxPages
        )
    }
}
