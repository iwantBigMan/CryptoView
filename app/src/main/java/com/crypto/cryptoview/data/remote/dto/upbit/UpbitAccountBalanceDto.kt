package com.crypto.cryptoview.data.remote.dto.upbit

import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpbitAccountBalanceDto(
    @SerialName("currency")
    val currency: String,

    @SerialName("balance")
    val balance: String,

    @SerialName("locked")
    val locked: String,

    @SerialName("avg_buy_price")
    val avgBuyPrice: String,

    @SerialName("avg_buy_price_modified")
    val avgBuyPriceModified: Boolean,

    @SerialName("unit_currency")
    val unitCurrency: String
) {
    fun toDomain() = UpbitAccountBalance(
        currency = currency,
        balance = balance.toDoubleOrNull() ?: 0.0,
        locked = locked.toDoubleOrNull() ?: 0.0,
        avgBuyPrice = avgBuyPrice.toDoubleOrNull() ?: 0.0,
        avgBuyPriceModified = avgBuyPriceModified,
        unitCurrency = unitCurrency
    )
}