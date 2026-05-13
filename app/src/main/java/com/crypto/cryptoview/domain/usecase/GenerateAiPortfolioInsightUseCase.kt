package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.mapper.toAiPortfolioSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsightStage
import com.crypto.cryptoview.domain.repository.AiPortfolioInsightRepository
import javax.inject.Inject

class GenerateAiPortfolioInsightUseCase @Inject constructor(
    private val getAllHoldingsUseCase: GetAllHoldingsUseCase,
    private val repository: AiPortfolioInsightRepository
) {
    suspend operator fun invoke(
        onStageChanged: (AiPortfolioInsightStage) -> Unit = {}
    ): Result<AiPortfolioInsight> {
        onStageChanged(AiPortfolioInsightStage.REFRESHING_ASSETS)

        val holdingsResult = getAllHoldingsUseCase(minValue = 1.0).getOrElse { throwable ->
            return Result.failure(throwable)
        }

        val snapshot = holdingsResult.toAiPortfolioSnapshot()
        if (snapshot.holdings.isEmpty()) {
            return Result.failure(IllegalStateException("포트폴리오 데이터가 없습니다."))
        }

        onStageChanged(AiPortfolioInsightStage.GENERATING_INSIGHT)
        return repository.generateInsight(snapshot)
    }
}
