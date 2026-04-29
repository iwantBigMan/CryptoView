package com.crypto.cryptoview.domain.model.asset

data class CommonBalance(
    val currency: String,
    val available: Double,
    val locked: Double
)
