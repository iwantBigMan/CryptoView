package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import com.crypto.cryptoview.domain.repository.UbbitMTickerRepository
import javax.inject.Inject

class GetUpbitMTickerUseCase @Inject constructor(
    private val repository: UbbitMTickerRepository
) {
    suspend operator fun invoke() : Result<List<UpbitMarketTicker>>{
        return repository.getMarketTickers()
    }

}