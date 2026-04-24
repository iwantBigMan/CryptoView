package com.crypto.cryptoview.domain.usecase.upbit

import com.crypto.cryptoview.domain.model.UpbitTickerAll
import com.crypto.cryptoview.domain.repository.UpbitTickerAllRepository
import javax.inject.Inject

class GetUpbitTickerAllUseCase @Inject constructor(
    private val repository: UpbitTickerAllRepository
) {
    suspend operator fun invoke() : Result<List<UpbitTickerAll>>{
        return repository.getAllMarkets()
    }
}