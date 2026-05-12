package com.crypto.cryptoview.domain.model.gate

import java.math.BigDecimal

data class GateIoSpotAveragePrice(
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
    val warnings: List<String>
) {
    val quantityValue: BigDecimal?
        get() = quantity.toBigDecimalOrNull()

    val currentQuantityValue: BigDecimal?
        get() = currentQuantity.toBigDecimalOrNull()

    val averagePriceValue: BigDecimal?
        get() = averagePrice.toBigDecimalOrNull()

    val totalCostValue: BigDecimal?
        get() = totalCost.toBigDecimalOrNull()
}

data class GateIoAveragePriceFees(
    val baseCurrency: String,
    val quoteCurrency: String,
    val other: List<GateIoOtherFee>
)

data class GateIoOtherFee(
    val currency: String,
    val amount: String
)
