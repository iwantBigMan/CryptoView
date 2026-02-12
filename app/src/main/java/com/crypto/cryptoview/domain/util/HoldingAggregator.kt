package com.crypto.cryptoview.domain.util

import com.crypto.cryptoview.domain.model.AggregatedHolding
import com.crypto.cryptoview.domain.model.HoldingData

/**
 * 여러 거래소의 HoldingData를 심볼 기준으로 통합하는 유틸리티
 */
object HoldingAggregator {

    /**
     * 거래소별 HoldingData 리스트를 심볼 기준으로 통합
     *
     * @param holdings 모든 거래소의 HoldingData 리스트
     * @return 심볼별로 통합된 AggregatedHolding 리스트
     */
    fun aggregate(holdings: List<HoldingData>): List<AggregatedHolding> {
        // 정규화된 심볼 기준으로 그룹핑
        val groupedBySymbol = holdings.groupBy { holding ->
            SymbolNormalizer.normalize(holding.symbol)
        }

        return groupedBySymbol.map { (normalizedSymbol, holdingList) ->
            createAggregatedHolding(normalizedSymbol, holdingList)
        }.sortedByDescending { it.totalValue }
    }

    /**
     * 같은 심볼의 HoldingData들을 하나의 AggregatedHolding으로 통합
     */
    private fun createAggregatedHolding(
        normalizedSymbol: String,
        holdings: List<HoldingData>
    ): AggregatedHolding {
        val totalBalance = holdings.sumOf { it.balance }
        val totalValue = holdings.sumOf { it.totalValue }
        val totalChange = holdings.sumOf { it.change }

        // 총 변화율 계산: (현재가치 - 매수가치) / 매수가치 * 100
        val totalBuyValue = totalValue - totalChange
        val totalChangePercent = if (totalBuyValue > 0) {
            (totalChange / totalBuyValue) * 100
        } else {
            0.0
        }

        // 이름은 첫 번째 홀딩에서 가져옴 (보통 동일)
        val name = holdings.firstOrNull()?.name ?: normalizedSymbol

        return AggregatedHolding(
            normalizedSymbol = normalizedSymbol,
            name = name,
            totalBalance = totalBalance,
            totalValue = totalValue,
            totalChange = totalChange,
            totalChangePercent = totalChangePercent,
            holdings = holdings.sortedByDescending { it.totalValue }
        )
    }

    /**
     * 특정 심볼의 통합 데이터만 조회
     *
     * @param holdings 모든 HoldingData 리스트
     * @param symbol 조회할 심볼
     * @return 해당 심볼의 AggregatedHolding (없으면 null)
     */
    fun aggregateBySymbol(holdings: List<HoldingData>, symbol: String): AggregatedHolding? {
        val normalizedSymbol = SymbolNormalizer.normalize(symbol)
        val matchingHoldings = holdings.filter {
            SymbolNormalizer.normalize(it.symbol) == normalizedSymbol
        }

        return if (matchingHoldings.isNotEmpty()) {
            createAggregatedHolding(normalizedSymbol, matchingHoldings)
        } else {
            null
        }
    }

    /**
     * 0원 이하 자산 필터링 후 통합
     *
     * @param holdings 모든 HoldingData 리스트
     * @param minValue 최소 금액 (기본값: 1원)
     * @return 필터링 및 통합된 리스트
     */
    fun aggregateFiltered(
        holdings: List<HoldingData>,
        minValue: Double = 1.0
    ): List<AggregatedHolding> {
        // 변경: 기존 > 를 >= 로 변경하여 정확히 1원인 자산도 포함
        val filteredHoldings = holdings.filter { it.totalValue >= minValue }
        return aggregate(filteredHoldings)
    }
}
