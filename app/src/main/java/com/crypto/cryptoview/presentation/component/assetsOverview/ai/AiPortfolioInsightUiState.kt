package com.crypto.cryptoview.presentation.component.assetsOverview.ai

import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight

sealed interface AiPortfolioInsightUiState {
    data object Idle : AiPortfolioInsightUiState
    data object RefreshingAssets : AiPortfolioInsightUiState
    data object GeneratingInsight : AiPortfolioInsightUiState
    data class Success(
        val insight: AiPortfolioInsight,
        val insightParagraphs: List<String>
    ) : AiPortfolioInsightUiState
    data class Error(val message: String) : AiPortfolioInsightUiState
}

