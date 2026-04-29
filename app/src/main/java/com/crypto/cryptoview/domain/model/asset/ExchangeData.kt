package com.crypto.cryptoview.domain.model.asset

import com.crypto.cryptoview.domain.model.exchange.ExchangeType



data class ExchangeData(
    val exchange: ExchangeType,
    val totalValue: Double
)
