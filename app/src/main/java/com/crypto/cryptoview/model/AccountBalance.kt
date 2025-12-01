package com.crypto.cryptoview.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountBalance(
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
)