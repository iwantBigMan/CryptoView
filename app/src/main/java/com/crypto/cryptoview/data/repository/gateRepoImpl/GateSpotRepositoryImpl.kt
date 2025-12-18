package com.crypto.cryptoview.data.repository.gateRepoImpl

import com.crypto.cryptoview.data.remote.api.GateSpotApi
import com.crypto.cryptoview.domain.model.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker
import com.crypto.cryptoview.domain.model.gate.toDomain // 수정된 import
import com.crypto.cryptoview.domain.model.toDomain
import com.crypto.cryptoview.domain.repository.GateSpotRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class GateSpotRepositoryImpl @Inject constructor(
    private val api: GateSpotApi
) : GateSpotRepository {
    override suspend fun getSpotBalances(): Result<List<GateSpotBalance>> = runCatching {
        api.getSpotBalances().map { dto -> dto.toDomain() }
    }

    override suspend fun getSpotTickers(currencyPair: String): Result<List<GateSpotTicker>> = runCatching {
        api.getSpotTickers("currency_pair").map { dto -> dto.toDomain() }
    }
}