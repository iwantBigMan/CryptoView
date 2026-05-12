package com.crypto.cryptoview.data.remote.dto.gateio

import kotlinx.serialization.Serializable

@Serializable
data class GateIoSpotAveragePriceRequest(
    val currencyPair: String,
    val from: Long? = null,
    val to: Long? = null,
    val maxPages: Int? = null
)

@Serializable
data class GateIoSpotAveragePriceResponse(
    val currencyPair: String,
    val baseCurrency: String,
    val quoteCurrency: String,
    val quantity: String,
    val currentQuantity: String,
    val averagePrice: String,
    val totalCost: String,
    val tradeCount: Int,
    val fetchedPages: Int,
    val fees: GateIoAveragePriceFees,
    val warnings: List<String> = emptyList()
)

@Serializable
data class GateIoAveragePriceFees(
    val baseCurrency: String,
    val quoteCurrency: String,
    val other: List<GateIoOtherFee> = emptyList()
)

@Serializable
data class GateIoOtherFee(
    val currency: String,
    val amount: String
)
