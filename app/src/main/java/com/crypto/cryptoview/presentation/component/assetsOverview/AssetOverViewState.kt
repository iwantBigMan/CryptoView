package com.crypto.cryptoview.presentation.component.assetsOverview

import com.crypto.cryptoview.domain.model.asset.AggregatedHolding
import com.crypto.cryptoview.domain.model.asset.ExchangeData
import com.crypto.cryptoview.domain.model.asset.HoldingData

/**
 * 자산 개요 화면의 UI 상태
 *
 * @property totalValue 전체 자산 평가금액 (KRW)
 * @property totalChange 전체 손익금액
 * @property totalChangeRate 전체 손익률 (%)
 * @property topAggregatedHoldings 심볼 기준 통합된 Top 5 홀딩 (홈 화면용)
 * @property allHoldings 거래소별 개별 홀딩 데이터 (디테일 화면용)
 * @property exchangeBreakdown 거래소별 자산 요약
 * @property isLoading 로딩 중 여부
 * @property error 에러 메시지
 */
data class MainUiState(
    val totalValue: Double = 0.0,
    val totalChange: Double = 0.0,
    val totalChangeRate: Double = 0.0,
    val usdtKrwRate: Double = 1300.0,
    val topAggregatedHoldings: List<AggregatedHolding> = emptyList(),  // 통합 홀딩 (Top 5)
    val allHoldings: List<HoldingData> = emptyList(),                  // 원본 홀딩 (거래소별)
    val exchangeBreakdown: List<ExchangeData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /**
     * 기존 topHoldings 호환성 유지 (deprecated)
     * 통합된 홀딩에서 첫 번째 거래소 데이터 반환
     */
    @Deprecated("Use topAggregatedHoldings instead", ReplaceWith("topAggregatedHoldings"))
    val topHoldings: List<HoldingData>
        get() = topAggregatedHoldings.flatMap { it.holdings }.take(5)
}



