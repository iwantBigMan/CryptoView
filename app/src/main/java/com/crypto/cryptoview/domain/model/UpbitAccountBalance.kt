package com.crypto.cryptoview.domain.model

data class UpbitAccountBalance(
    val currency: String,
    val balance: Double,
    val locked: Double,
    val avgBuyPrice: Double,
    val avgBuyPriceModified: Boolean,
    val unitCurrency: String
) {
    val availableBalance: Double
        get() = balance - locked

    val totalValue: Double
        get() = balance * avgBuyPrice
}