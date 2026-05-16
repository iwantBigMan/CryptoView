package com.crypto.cryptoview.domain.usecase.ai

import com.crypto.cryptoview.domain.mapper.ai.toAiPortfolioSnapshot
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsightStage
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import com.crypto.cryptoview.domain.repository.AiPortfolioInsightRepository
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase
import javax.inject.Inject

class GenerateAiPortfolioInsightUseCase @Inject constructor(
    private val getAllHoldingsUseCase: GetAllHoldingsUseCase,
    private val repository: AiPortfolioInsightRepository
) {
    suspend operator fun invoke(
        displayCurrency: DisplayCurrency,
        onStageChanged: (AiPortfolioInsightStage) -> Unit = {}
    ): Result<AiPortfolioInsight> {
        onStageChanged(AiPortfolioInsightStage.REFRESHING_ASSETS)

        val holdingsResult = getAllHoldingsUseCase(minValue = 1.0).getOrElse { throwable ->
            return Result.failure(throwable)
        }

        val snapshot = holdingsResult.toAiPortfolioSnapshot(displayCurrency)
        if (snapshot.portfolioSummary.holdingsCount == 0) {
            return Result.failure(IllegalStateException("포트폴리오 데이터가 없습니다."))
        }

        onStageChanged(AiPortfolioInsightStage.GENERATING_INSIGHT)
        return repository.generateInsight(snapshot)
    }
}
