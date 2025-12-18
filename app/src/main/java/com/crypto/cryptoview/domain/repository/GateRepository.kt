package com.crypto.cryptoview.domain.repository

// File: 'app/src/main/java/com/crypto/cryptoview/domain/repository/GateSpotRepository.kt'


import com.crypto.cryptoview.domain.model.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateFuturesAccount
import com.crypto.cryptoview.domain.model.gate.GateFuturesPosition
import com.crypto.cryptoview.domain.model.gate.GateFuturesTicker
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker

interface GateSpotRepository {
    suspend fun getSpotBalances(): Result<List<GateSpotBalance>>
    suspend fun getSpotTickers(currencyPair: String): Result<List<GateSpotTicker>>
}

interface GateFuturesRepository {
    suspend fun getFuturesAccount(): Result<GateFuturesAccount>
    suspend fun getPositions(): Result<List<GateFuturesPosition>>
    suspend fun getFuturesTickers(): Result<List<GateFuturesTicker>>
}