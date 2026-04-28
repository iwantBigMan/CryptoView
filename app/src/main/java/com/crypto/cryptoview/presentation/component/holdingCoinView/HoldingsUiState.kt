package com.crypto.cryptoview.presentation.component.holdingCoinView

import com.crypto.cryptoview.domain.model.asset.AggregatedHolding
import com.crypto.cryptoview.domain.model.asset.HoldingData
import com.crypto.cryptoview.presentation.component.holdingCoinView.preview.SortType

/**
 * 보유 코인 화면 UI 상태
 *
 * @property allHoldings 거래소별 개별 홀딩 (디테일용)
 * @property aggregatedHoldings 심볼 기준 통합 홀딩 (메인 목록용)
 * @property filteredAggregatedHoldings 필터링된 통합 홀딩
 * @property searchQuery 검색어
 * @property sortType 정렬 타입
 * @property isLoading 로딩 상태
 */
data class HoldingsUiState(
    val allHoldings: List<HoldingData> = emptyList(),
    val aggregatedHoldings: List<AggregatedHolding> = emptyList(),
    val filteredAggregatedHoldings: List<AggregatedHolding> = emptyList(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.VALUE,
    val isLoading: Boolean = false
) {
    /**
     * 기존 filteredHoldings 호환성 유지 (deprecated)
     */
    @Deprecated("Use filteredAggregatedHoldings instead")
    val filteredHoldings: List<HoldingData>
        get() = filteredAggregatedHoldings.flatMap { it.holdings }
}
