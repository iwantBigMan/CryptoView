package com.crypto.cryptoview.domain.model.asset

/**
 * 해외 거래소 잔고 모델
 * @property asset 자산 심볼
 * @property free 사용 가능 잔고
 * @property locked 잠긴 잔고
 */
data class ForeignBalance(
    val asset: String,
    val free: Double,
    val locked: Double = 0.0
)

