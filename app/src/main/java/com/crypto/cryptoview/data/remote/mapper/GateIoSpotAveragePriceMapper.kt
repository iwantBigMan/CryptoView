package com.crypto.cryptoview.data.remote.mapper

import com.crypto.cryptoview.data.remote.dto.gateio.GateIoAveragePriceFees
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoOtherFee
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoSpotAveragePriceResponse
import com.crypto.cryptoview.domain.model.gate.GateIoSpotAveragePrice

fun GateIoSpotAveragePriceResponse.toDomain(): GateIoSpotAveragePrice {
    return GateIoSpotAveragePrice(
        currencyPair = currencyPair,
        baseCurrency = baseCurrency,
        quoteCurrency = quoteCurrency,
        quantity = quantity,
        currentQuantity = currentQuantity,
        averagePrice = averagePrice,
        totalCost = totalCost,
        tradeCount = tradeCount,
        fetchedPages = fetchedPages,
        fees = fees.toDomain(),
        warnings = warnings
    )
}

private fun GateIoAveragePriceFees.toDomain(): com.crypto.cryptoview.domain.model.gate.GateIoAveragePriceFees {
    return com.crypto.cryptoview.domain.model.gate.GateIoAveragePriceFees(
        baseCurrency = baseCurrency,
        quoteCurrency = quoteCurrency,
        other = other.map { it.toDomain() }
    )
}

private fun GateIoOtherFee.toDomain(): com.crypto.cryptoview.domain.model.gate.GateIoOtherFee {
    return com.crypto.cryptoview.domain.model.gate.GateIoOtherFee(
        currency = currency,
        amount = amount
    )
}
