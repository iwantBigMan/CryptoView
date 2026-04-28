package com.crypto.cryptoview.presentation.settings

import com.crypto.cryptoview.domain.model.exchange.ExchangeType

/**
 * 거래소 연동 설정 UI 상태
 */
data class ExchangeInput(
    val apiKey: String = "",
    val secretKey: String = ""
)

data class ExchangeSettingsUiState(
    val selectedExchange: ExchangeType = ExchangeType.UPBIT,
    val selectedExchanges: Set<ExchangeType> = emptySet(),
    val inputs: Map<ExchangeType, ExchangeInput> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val savedCredentials: List<ExchangeType> = emptyList(),
    val saveSuccess: Boolean = false
)

