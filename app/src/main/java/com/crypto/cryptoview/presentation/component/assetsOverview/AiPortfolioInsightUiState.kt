package com.crypto.cryptoview.presentation.component.assetsOverview

import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsight

sealed interface AiPortfolioInsightUiState {
    data object Idle : AiPortfolioInsightUiState
    data object RefreshingAssets : AiPortfolioInsightUiState
    data object GeneratingInsight : AiPortfolioInsightUiState
    data class Success(val insight: AiPortfolioInsight) : AiPortfolioInsightUiState
    data class Error(val message: String) : AiPortfolioInsightUiState
}

