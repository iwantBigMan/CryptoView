package com.crypto.cryptoview.domain.repository



import com.crypto.cryptoview.domain.model.gate.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateFuturesAccount
import com.crypto.cryptoview.domain.model.gate.GateFuturesPosition
import com.crypto.cryptoview.domain.model.gate.GateFuturesTicker
import com.crypto.cryptoview.domain.model.gate.GateIoSpotAveragePrice
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker

interface GateSpotRepository {
    suspend fun getSpotBalances(): Result<List<GateSpotBalance>>
    suspend fun getSpotTickers(currencyPairs: List<String>): Result<List<GateSpotTicker>>
    suspend fun getSpotAveragePrice(
        currencyPair: String,
        from: Long? = null,
        to: Long? = null,
        maxPages: Int? = null
    ): Result<GateIoSpotAveragePrice>
}

interface GateFuturesRepository {
    suspend fun getFuturesAccount(): Result<GateFuturesAccount>
    suspend fun getPositions(): Result<List<GateFuturesPosition>>
    suspend fun getFuturesTickers(): Result<List<GateFuturesTicker>>
}
