package com.crypto.cryptoview.presentation.model

import com.crypto.cryptoview.domain.model.asset.CurrencyUnit
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import java.util.Locale

fun formatDisplayMoney(
    krwValue: Double,
    displayCurrency: DisplayCurrency,
    usdtKrwRate: Double,
    signed: Boolean = false
): String {
    val sign = if (signed && krwValue > 0.0) "+" else ""
    return when (displayCurrency) {
        DisplayCurrency.KRW -> String.format(
            Locale.getDefault(),
            "%s₩%,.0f",
            sign,
            krwValue
        )
        DisplayCurrency.USDT -> String.format(
            Locale.getDefault(),
            "%s%,.2f USDT",
            sign,
            krwToUsdt(krwValue, usdtKrwRate)
        )
    }
}

fun formatDisplayPrice(
    value: Double,
    sourceUnit: CurrencyUnit,
    displayCurrency: DisplayCurrency,
    usdtKrwRate: Double
): String {
    val krwValue = when (sourceUnit) {
        CurrencyUnit.KRW -> value
        CurrencyUnit.USDT -> value * usdtKrwRate
    }
    return formatDisplayMoney(
        krwValue = krwValue,
        displayCurrency = displayCurrency,
        usdtKrwRate = usdtKrwRate
    )
}

private fun krwToUsdt(krwValue: Double, usdtKrwRate: Double): Double {
    return if (usdtKrwRate > 0.0) krwValue / usdtKrwRate else 0.0
}
