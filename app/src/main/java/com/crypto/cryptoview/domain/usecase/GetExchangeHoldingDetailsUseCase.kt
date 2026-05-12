package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.asset.CurrencyUnit
import com.crypto.cryptoview.domain.model.asset.ExchangeHoldingDetail
import com.crypto.cryptoview.domain.model.asset.HoldingData
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import javax.inject.Inject

/**
 * 거래소별 보유 상세 조회 결과
 * UseCase에서 모든 계산을 완료하여 반환
 */
data class ExchangeHoldingResult(
    val symbol: String,
    val coinName: String,
    val totalValueKrw: Double,
    val totalProfitLoss: Double?,
    val totalProfitLossPercent: Double?,
    val exchangeHoldings: List<ExchangeHoldingDetail>
)

/**
 * 코인 심볼 기준으로 거래소별 보유 상세 정보를 조회하는 UseCase
 */
class GetExchangeHoldingDetailsUseCase @Inject constructor() {

    /**
     * 심볼 기준으로 거래소별 보유 상세 정보와 총합 계산 결과를 반환
     */
    operator fun invoke(
        symbol: String,
        allHoldings: List<HoldingData>,
        usdtKrwRate: Double
    ): ExchangeHoldingResult {
        val exchangeDetails = allHoldings
            .filter { it.symbol.equals(symbol, ignoreCase = true) }
            .map { holding ->
                val currencyUnit = getCurrencyUnit(holding.exchange)
                val avgBuyPrice = calculateAvgBuyPrice(holding)
                val displayAvgBuyPrice = convertPriceForCurrencyUnit(avgBuyPrice, currencyUnit, usdtKrwRate)
                val displayCurrentPrice = convertPriceForCurrencyUnit(holding.currentPrice, currencyUnit, usdtKrwRate)
                val profitLoss = calculateProfitLoss(holding, avgBuyPrice)
                val profitLossPercent = calculateProfitLossPercent(holding, avgBuyPrice)

                ExchangeHoldingDetail(
                    exchange = holding.exchange,
                    symbol = holding.symbol,
                    quantity = holding.balance,
                    avgBuyPrice = displayAvgBuyPrice,
                    currentPrice = displayCurrentPrice,
                    currencyUnit = currencyUnit,
                    valueKrw = holding.totalValue,
                    profitLoss = profitLoss,
                    profitLossPercent = profitLossPercent
                )
            }
            .sortedByDescending { it.valueKrw }

        val totalValueKrw = exchangeDetails.sumOf { it.valueKrw }
        val totalProfitLoss = exchangeDetails
            .mapNotNull { it.profitLoss }
            .takeIf { it.isNotEmpty() }
            ?.sum()
        val totalProfitLossPercent = calculateTotalProfitLossPercent(totalValueKrw, totalProfitLoss)
        val coinName = exchangeDetails.firstOrNull()?.symbol ?: symbol

        return ExchangeHoldingResult(
            symbol = symbol,
            coinName = coinName,
            totalValueKrw = totalValueKrw,
            totalProfitLoss = totalProfitLoss,
            totalProfitLossPercent = totalProfitLossPercent,
            exchangeHoldings = exchangeDetails
        )
    }

    private fun calculateTotalProfitLossPercent(totalValueKrw: Double, totalProfitLoss: Double?): Double? {
        if (totalProfitLoss == null || totalValueKrw <= 0) return null
        val buyValue = totalValueKrw - totalProfitLoss
        return if (buyValue > 0) (totalProfitLoss / buyValue) * 100 else null
    }

    private fun getCurrencyUnit(exchange: ExchangeType): CurrencyUnit {
        return when (exchange) {
            ExchangeType.UPBIT -> CurrencyUnit.KRW
            else -> CurrencyUnit.USDT
        }
    }

    private fun convertPriceForCurrencyUnit(
        priceKrw: Double?,
        currencyUnit: CurrencyUnit,
        usdtKrwRate: Double
    ): Double? {
        if (priceKrw == null) return null
        return when {
            currencyUnit == CurrencyUnit.USDT && usdtKrwRate > 0.0 -> priceKrw / usdtKrwRate
            else -> priceKrw
        }
    }

    private fun convertPriceForCurrencyUnit(
        priceKrw: Double,
        currencyUnit: CurrencyUnit,
        usdtKrwRate: Double
    ): Double {
        return when {
            currencyUnit == CurrencyUnit.USDT && usdtKrwRate > 0.0 -> priceKrw / usdtKrwRate
            else -> priceKrw
        }
    }

    private fun calculateAvgBuyPrice(holding: HoldingData): Double? {
        return holding.avgBuyPrice?.takeIf { it > 0.0 }
    }

    private fun calculateProfitLoss(holding: HoldingData, avgBuyPrice: Double?): Double? {
        return if (avgBuyPrice != null && avgBuyPrice > 0) {
            holding.change
        } else {
            null
        }
    }

    private fun calculateProfitLossPercent(holding: HoldingData, avgBuyPrice: Double?): Double? {
        return if (avgBuyPrice != null && avgBuyPrice > 0) {
            holding.changePercent
        } else {
            null
        }
    }
}
