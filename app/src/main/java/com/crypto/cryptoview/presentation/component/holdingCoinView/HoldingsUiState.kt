package com.crypto.cryptoview.presentation.component.holdingCoinView

import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.presentation.component.holdingCoinView.preview.SortType

/**
 * 보유 코인 화면 UI 상태
 * @property allHoldings 전체 보유 자산
 * @property filteredHoldings 필터링된 보유 자산
 * @property searchQuery 검색어
 * @property sortType 정렬 타입
 * @property isLoading 로딩 상태
 */
data class HoldingsUiState(
    val allHoldings: List<HoldingData> = emptyList(),
    val filteredHoldings: List<HoldingData> = emptyList(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.VALUE,
    val isLoading: Boolean = false
)