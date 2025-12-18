package com.crypto.cryptoview.domain.model

data class CommonBalance(
    val currency: String,
    val available: Double,
    val locked: Double
)