package com.crypto.cryptoview.data.remote.dto.upbit

import com.crypto.cryptoview.domain.model.UpbitMarketTicker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpbitMarketTickerDto(
    @SerialName("market") val market: String,
    @SerialName("trade_date") val tradeDate: String,
    @SerialName("trade_time") val tradeTime: String,
    @SerialName("trade_date_kst") val tradeDateKst: String,
    @SerialName("trade_time_kst") val tradeTimeKst: String,
    @SerialName("trade_timestamp") val tradeTimestamp: Long,
    @SerialName("opening_price") val openingPrice: Double,
    @SerialName("high_price") val highPrice: Double,
    @SerialName("low_price") val lowPrice: Double,
    @SerialName("trade_price") val tradePrice: Double,
    @SerialName("prev_closing_price") val prevClosingPrice: Double,
    @SerialName("change") val change: String,
    @SerialName("change_price") val changePrice: Double,
    @SerialName("change_rate") val changeRate: Double,
    @SerialName("signed_change_price") val signedChangePrice: Double,
    @SerialName("signed_change_rate") val signedChangeRate: Double,
    @SerialName("trade_volume") val tradeVolume: Double,
    @SerialName("acc_trade_price") val accTradePrice: Double,
    @SerialName("acc_trade_price_24h") val accTradePrice24h: Double,
    @SerialName("acc_trade_volume") val accTradeVolume: Double,
    @SerialName("acc_trade_volume_24h") val accTradeVolume24h: Double,
    @SerialName("highest_52_week_price") val highest52WeekPrice: Double,
    @SerialName("highest_52_week_date") val highest52WeekDate: String,
    @SerialName("lowest_52_week_price") val lowest52WeekPrice: Double,
    @SerialName("lowest_52_week_date") val lowest52WeekDate: String,
    @SerialName("timestamp") val timestamp: Long
) {
    fun toDomain() = UpbitMarketTicker(
        market = market,
        tradePrice = tradePrice,
        change = change,
        changeRate = changeRate,
        signedChangeRate = signedChangeRate,
        highPrice = highPrice,
        lowPrice = lowPrice,
        accTradePrice24h = accTradePrice24h,
        accTradeVolume24h = accTradeVolume24h,
        timestamp = timestamp
    )
}