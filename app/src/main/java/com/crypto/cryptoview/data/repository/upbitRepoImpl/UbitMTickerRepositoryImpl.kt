package com.crypto.cryptoview.data.repository.upbitRepoImpl

import com.crypto.cryptoview.data.remote.api.UpbitMarketApi
import com.crypto.cryptoview.domain.model.upbit.UpbitMarketTicker
import com.crypto.cryptoview.domain.repository.UbbitMTickerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UbitMTickerRepositoryImpl @Inject constructor(
    private val api: UpbitMarketApi
) : UbbitMTickerRepository {
    override suspend fun getMarketTickers(currencies: List<String>): Result<List<UpbitMarketTicker>> = runCatching {
        // 잔고 기반 마켓 목록 생성 (KRW 제외)
        val markets = currencies
            .filter { it != "KRW" }
            .map { "KRW-$it" }
            .toMutableList()
            .apply {
                if (!contains("KRW-USDT")) add("KRW-USDT") // 환율 계산용 USDT 항상 포함
            }
            .joinToString(",")

        if (markets.isEmpty()) {
            emptyList()
        } else {
            api.getTickers(markets = markets).map { it.toDomain() }
        }
    }
}