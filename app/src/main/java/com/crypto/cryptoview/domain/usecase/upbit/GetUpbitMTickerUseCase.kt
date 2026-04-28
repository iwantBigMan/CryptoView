package com.crypto.cryptoview.domain.usecase.upbit

import com.crypto.cryptoview.domain.model.upbit.UpbitMarketTicker
import com.crypto.cryptoview.domain.repository.UbbitMTickerRepository
import javax.inject.Inject

class GetUpbitMTickerUseCase @Inject constructor(
    private val repository: UbbitMTickerRepository
) {
    suspend operator fun invoke(currencies: List<String>): Result<List<UpbitMarketTicker>> {
        return repository.getMarketTickers(currencies)
    }

}