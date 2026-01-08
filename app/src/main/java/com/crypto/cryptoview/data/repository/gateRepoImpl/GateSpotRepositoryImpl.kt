package com.crypto.cryptoview.data.repository.gateRepoImpl

import com.crypto.cryptoview.data.remote.api.GateSpotApi
import com.crypto.cryptoview.domain.model.GateSpotBalance
import com.crypto.cryptoview.domain.model.gate.GateSpotTicker
import com.crypto.cryptoview.domain.model.gate.toDomain
import com.crypto.cryptoview.domain.model.toDomain
import com.crypto.cryptoview.domain.repository.GateSpotRepository
import javax.inject.Inject

class GateSpotRepositoryImpl @Inject constructor(
    private val gateIOApi: GateSpotApi
) : GateSpotRepository {

    override suspend fun getSpotBalances(): Result<List<GateSpotBalance>> {
        return try {
            val response = gateIOApi.getSpotBalances()
            val balances = response
                .filter { (it.available.toDoubleOrNull() ?: 0.0) > 0 }
                .map { it.toDomain() }
            Result.success(balances)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSpotTickers(currencyPairs: List<String>): Result<List<GateSpotTicker>> {
        return try {
            if (currencyPairs.isEmpty()) {
                // 전체 티커 조회
                val response = gateIOApi.getSpotTickers(null)
                Result.success(response.map { it.toDomain() })
            } else {
                // 특정 페어만 조회
                val tickers = currencyPairs.mapNotNull { pair ->
                    try {
                        gateIOApi.getSpotTickers(pair).firstOrNull()?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                }
                Result.success(tickers)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}