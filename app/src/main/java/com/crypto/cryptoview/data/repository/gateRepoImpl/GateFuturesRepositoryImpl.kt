package com.crypto.cryptoview.data.repository.gateRepoImpl

import com.crypto.cryptoview.data.remote.api.GateFuturesApi
import com.crypto.cryptoview.domain.model.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateFuturesAccount
import com.crypto.cryptoview.domain.model.gate.GateFuturesPosition
import com.crypto.cryptoview.domain.model.gate.GateFuturesTicker
import com.crypto.cryptoview.domain.model.gate.toDomain
import com.crypto.cryptoview.domain.repository.GateFuturesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GateFuturesRepositoryImpl @Inject constructor(
    private val api: GateFuturesApi
) : GateFuturesRepository {
    override suspend fun getFuturesAccount(): Result<GateFuturesAccount> = runCatching {
        api.getFuturesAccount().toDomain()
    }

    override suspend fun getPositions(): Result<List<GateFuturesPosition>> = runCatching {
        api.getPositions().map { dto -> dto.toDomain() }
    }

    override suspend fun getFuturesTickers(): Result<List<GateFuturesTicker>> = runCatching {
        api.getFuturesTickers().map { dto -> dto.toDomain() }
    }
}