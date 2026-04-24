package com.crypto.cryptoview.data.repository.upbitRepoImpl

import com.crypto.cryptoview.data.remote.api.UpbitTickerAllApi
import com.crypto.cryptoview.domain.model.UpbitTickerAll
import com.crypto.cryptoview.domain.repository.UpbitTickerAllRepository
import javax.inject.Inject

class UpbitTickerAllRepositoryimpl @Inject constructor(
    private val api : UpbitTickerAllApi
) : UpbitTickerAllRepository {
    override suspend fun getAllMarkets(): Result<List<UpbitTickerAll>> = runCatching {
        api.getAllTickers("KRW").map { it.toDomain() }
    }
}