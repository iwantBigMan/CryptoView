package com.crypto.cryptoview.data.repository.upbitRepoImpl

import com.crypto.cryptoview.data.remote.api.UpbitApi
import com.crypto.cryptoview.data.remote.api.UpbitMarketApi
import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import com.crypto.cryptoview.domain.repository.UbbitMTickerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UbitMTickerRepositoryImpl @Inject constructor(
    private val api: UpbitMarketApi,
    private val upbitApi: UpbitApi
) : UbbitMTickerRepository {
    override suspend fun getMarketTickers(): Result<List<UpbitMarketTicker>> = runCatching {
        // 1. 보유 자산 조회
        val accounts = upbitApi.getUpbitAccountBalances()

        // 2. KRW 제외, 잔고 있는 코인만 필터링
        val markets = accounts
            .filter { it.currency != "KRW" && (it.balance.toDoubleOrNull() ?: 0.0) > 0 }
            .map { "KRW-${it.currency}" }
            .toMutableList()
            .apply {
                if (!contains("KRW-USDT")) {
                    add("KRW-USDT")  // USDT는 항상 포함
                }
            }
            .joinToString(",")

        // 3. 티커 조회
        if (markets.isEmpty()) {
            emptyList()
        } else {
            api.getTickers(markets = markets).map { it.toDomain() }
        }
    }
}