package com.crypto.cryptoview.data.remote.dto.gateio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GateSpotBalanceDto(
    @SerialName("currency")
    val currency: String,

    @SerialName("available")
    val available: String,

    @SerialName("locked")
    val locked: String,

    @SerialName("update_id")
    val updateId: Long? = null,

    @SerialName("refresh_time")
    val refreshTime: Long? = null,

    @SerialName("avg_buy_price")
    val avgBuyPrice: JsonElement? = null,

    @SerialName("avgBuyPrice")
    val avgBuyPriceCamel: JsonElement? = null,

    @SerialName("avg_buy_price_usdt")
    val avgBuyPriceUsdt: JsonElement? = null,

    @SerialName("avgBuyPriceUsdt")
    val avgBuyPriceUsdtCamel: JsonElement? = null,

    @SerialName("average_buy_price")
    val averageBuyPrice: JsonElement? = null,

    @SerialName("averageBuyPrice")
    val averageBuyPriceCamel: JsonElement? = null
)

@Serializable
data class GateFuturesAccountDto(
    @SerialName("total")
    val total: String,

    @SerialName("available")
    val available: String,

    @SerialName("unrealised_pnl")
    val unrealisedPnl: String
)
