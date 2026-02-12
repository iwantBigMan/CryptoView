package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.CurrencyUnit
import com.crypto.cryptoview.domain.model.ExchangeHoldingDetail
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.model.HoldingData
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
 * 클린 아키텍처 준수: 도메인 레이어에서 비즈니스 로직 담당
 */
class GetExchangeHoldingDetailsUseCase @Inject constructor() {

    /**
     * 심볼 기준으로 거래소별 보유 상세 정보 + 총합 계산 결과 반환
     * @param symbol 조회할 코인 심볼
     * @param allHoldings 전체 보유 자산 리스트
     * @param usdtKrwRate USDT/KRW 환율
     * @return 거래소별 보유 상세 + 총합 계산 결과
     */
    operator fun invoke(
        symbol: String,
        allHoldings: List<HoldingData>,
        usdtKrwRate: Double
    ): ExchangeHoldingResult {
        // 거래소별 상세 정보 생성
        val exchangeDetails = allHoldings
            .filter { it.symbol.equals(symbol, ignoreCase = true) }
            .map { holding ->
                val currencyUnit = getCurrencyUnit(holding.exchange)
                val avgBuyPrice = calculateAvgBuyPrice(holding)
                val profitLoss = calculateProfitLoss(holding, avgBuyPrice)
                val profitLossPercent = calculateProfitLossPercent(holding, avgBuyPrice)

                ExchangeHoldingDetail(
                    exchange = holding.exchange,
                    symbol = holding.symbol,
                    quantity = holding.balance,
                    avgBuyPrice = avgBuyPrice,
                    currentPrice = holding.currentPrice,
                    currencyUnit = currencyUnit,
                    valueKrw = holding.totalValue,
                    profitLoss = profitLoss,
                    profitLossPercent = profitLossPercent
                )
            }
            .sortedByDescending { it.valueKrw }

        // 총합 계산 (비즈니스 로직 - UseCase에서 담당)
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

    /**
     * 총 손익률 계산
     */
    private fun calculateTotalProfitLossPercent(totalValueKrw: Double, totalProfitLoss: Double?): Double? {
        if (totalProfitLoss == null || totalValueKrw <= 0) return null
        val buyValue = totalValueKrw - totalProfitLoss
        return if (buyValue > 0) (totalProfitLoss / buyValue) * 100 else null
    }

    /**
     * 거래소에 따른 화폐 단위 결정
     */
    private fun getCurrencyUnit(exchange: ExchangeType): CurrencyUnit {
        return when (exchange) {
            ExchangeType.UPBIT -> CurrencyUnit.KRW
            else -> CurrencyUnit.USDT
        }
    }

    /**
     * 평균 매수가 계산
     * 업비트: API에서 제공
     * 해외 거래소: 현재 API에서 제공하지 않으므로 null 반환
     */
    private fun calculateAvgBuyPrice(holding: HoldingData): Double? {
        return when (holding.exchange) {
            ExchangeType.UPBIT -> {
                // 업비트는 change와 changePercent로 평단 역산 가능
                if (holding.changePercent != 0.0) {
                    val avgPrice = holding.currentPrice / (1 + holding.changePercent / 100)
                    if (avgPrice > 0) avgPrice else null
                } else {
                    holding.currentPrice // 변동률 0이면 현재가 = 평단
                }
            }
            else -> null // 해외 거래소는 평단 정보 없음
        }
    }

    /**
     * 손익 계산 (원화 기준)
     */
    private fun calculateProfitLoss(holding: HoldingData, avgBuyPrice: Double?): Double? {
        return if (avgBuyPrice != null && avgBuyPrice > 0) {
            holding.change
        } else {
            null
        }
    }

    /**
     * 손익률 계산
     */
    private fun calculateProfitLossPercent(holding: HoldingData, avgBuyPrice: Double?): Double? {
        return if (avgBuyPrice != null && avgBuyPrice > 0) {
            holding.changePercent
        } else {
            null
        }
    }
}
