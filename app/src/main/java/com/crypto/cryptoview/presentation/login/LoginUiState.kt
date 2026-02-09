package com.crypto.cryptoview.presentation.login

import com.crypto.cryptoview.domain.model.ExchangeType

/**
 * 로그인 화면 UI 상태
 */
data class ExchangeInput(
    val apiKey: String = "",
    val secretKey: String = ""
)

data class LoginUiState(
    val selectedExchange: ExchangeType = ExchangeType.UPBIT, // legacy single-select default
    val selectedExchanges: Set<ExchangeType> = emptySet(), // use Set for stable membership
    val inputs: Map<ExchangeType, ExchangeInput> = emptyMap(), // per-exchange inputs
    val isLoading: Boolean = false,
    val error: String? = null,
    val savedCredentials: List<ExchangeType> = emptyList(),
    val loginSuccess: Boolean = false // 화면에서 이 플래그를 관찰하여 안전하게 네비게이션 수행
)
