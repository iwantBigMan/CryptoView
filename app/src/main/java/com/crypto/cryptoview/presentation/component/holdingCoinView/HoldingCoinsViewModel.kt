package com.crypto.cryptoview.presentation.component.holdingCoinView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.AggregatedHolding
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase
import com.crypto.cryptoview.presentation.component.holdingCoinView.preview.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 보유 코인 화면의 ViewModel
 *
 * 책임:
 * - UI 상태 관리
 * - 검색/정렬 필터링
 * - 자동 갱신 제어
 *
 * 비즈니스 로직은 GetAllHoldingsUseCase에 위임
 */
@HiltViewModel
class HoldingCoinsViewModel @Inject constructor(
    private val getAllHoldingsUseCase: GetAllHoldingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null
    private var isAutoRefreshEnabled = true

    fun startAutoRefresh() {
        if (autoRefreshJob?.isActive == true) return

        isAutoRefreshEnabled = true

        autoRefreshJob = viewModelScope.launch {
            while (isActive && isAutoRefreshEnabled) {
                loadHoldings()
                delay(1000)
            }
        }
    }

    fun stopAutoRefresh() {
        isAutoRefreshEnabled = false
        autoRefreshJob?.cancel()
    }

    /**
     * 보유 자산 데이터 로드
     * UseCase에 모든 데이터 조회/계산 위임
     */
    private fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getAllHoldingsUseCase(minValue = 1.0)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        allHoldings = result.allHoldings,
                        aggregatedHoldings = result.aggregatedHoldings,
                        filteredAggregatedHoldings = applyFilters(
                            result.aggregatedHoldings,
                            _uiState.value.searchQuery,
                            _uiState.value.sortType
                        ),
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    android.util.Log.e("HoldingCoinsViewModel", "Error loading holdings", e)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredAggregatedHoldings = applyFilters(
                _uiState.value.aggregatedHoldings,
                query,
                _uiState.value.sortType
            )
        )
    }

    fun onSortTypeChange(sortType: SortType) {
        _uiState.value = _uiState.value.copy(
            sortType = sortType,
            filteredAggregatedHoldings = applyFilters(
                _uiState.value.aggregatedHoldings,
                _uiState.value.searchQuery,
                sortType
            )
        )
    }

    /**
     * 검색/정렬 필터 적용 (통합 홀딩용)
     * UI 레이어 로직이므로 ViewModel에서 처리
     */
    private fun applyFilters(
        holdings: List<AggregatedHolding>,
        query: String,
        sortType: SortType
    ): List<AggregatedHolding> {
        return holdings
            .filter {
                it.normalizedSymbol.contains(query, ignoreCase = true) ||
                it.name.contains(query, ignoreCase = true)
            }
            .let { filtered ->
                when (sortType) {
                    SortType.VALUE -> filtered.sortedByDescending { it.totalValue }
                    SortType.PROFIT -> filtered.sortedByDescending { it.totalChangePercent }
                    SortType.SYMBOL -> filtered.sortedBy { it.normalizedSymbol }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
