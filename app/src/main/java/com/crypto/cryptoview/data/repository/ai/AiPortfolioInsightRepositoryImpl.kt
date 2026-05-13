package com.crypto.cryptoview.data.repository.ai

import com.crypto.cryptoview.data.remote.api.AiPortfolioInsightApi
import com.crypto.cryptoview.data.remote.mapper.toDomain
import com.crypto.cryptoview.data.remote.mapper.toRequestDto
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSnapshot
import com.crypto.cryptoview.domain.repository.AiPortfolioInsightRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiPortfolioInsightRepositoryImpl @Inject constructor(
    private val api: AiPortfolioInsightApi
) : AiPortfolioInsightRepository {

    private companion object {
        const val CACHE_TTL_MILLIS = 5 * 60 * 1000L
    }

    private var lastSnapshot: AiPortfolioSnapshot? = null
    private var lastInsight: AiPortfolioInsight? = null
    private var lastGeneratedAt: Long = 0L

    override suspend fun generateInsight(snapshot: AiPortfolioSnapshot): Result<AiPortfolioInsight> {
        return runCatching {
            val now = System.currentTimeMillis()
            val cachedInsight = lastInsight
            val isCacheValid = lastSnapshot == snapshot &&
                cachedInsight != null &&
                now - lastGeneratedAt <= CACHE_TTL_MILLIS

            if (isCacheValid) {
                return@runCatching cachedInsight
            }

            val response = api.generatePortfolioInsight(snapshot.toRequestDto())
            val insight = response.toDomain(generatedAt = now)

            lastSnapshot = snapshot
            lastInsight = insight
            lastGeneratedAt = now

            insight
        }
    }
}

