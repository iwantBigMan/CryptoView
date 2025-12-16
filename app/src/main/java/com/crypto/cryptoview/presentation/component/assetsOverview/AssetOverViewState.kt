package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.compose.ui.graphics.Color
import com.crypto.cryptoview.domain.model.ExchangeData
import com.crypto.cryptoview.domain.model.HoldingData

data class MainUiState(
    val totalValue: Double = 0.0,
    val totalChange: Double = 0.0,
    val totalChangeRate: Double = 0.0,
    val topHoldings: List<HoldingData> = emptyList(),
    val exchangeBreakdown: List<ExchangeData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)



