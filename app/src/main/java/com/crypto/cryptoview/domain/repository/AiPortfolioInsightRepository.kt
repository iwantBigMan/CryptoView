package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight
import com.crypto.cryptoview.domain.model.ai.AiPortfolioSnapshot

interface AiPortfolioInsightRepository {
    suspend fun generateInsight(snapshot: AiPortfolioSnapshot): Result<AiPortfolioInsight>
}

