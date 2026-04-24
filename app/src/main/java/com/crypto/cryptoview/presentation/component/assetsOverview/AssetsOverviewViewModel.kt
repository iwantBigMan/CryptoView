package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase
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
 * 자산 개요 화면의 ViewModel
 *
 * 책임:
 * - UI 상태 관리
 * - 자동 갱신 제어
 *
 * 비즈니스 로직은 GetAllHoldingsUseCase에 위임
 */
@HiltViewModel
class AssetsOverviewViewModel @Inject constructor(
    private val getAllHoldingsUseCase: GetAllHoldingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null
    private var isAutoRefreshEnabled = true

    init {
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        if (autoRefreshJob?.isActive == true) return

        isAutoRefreshEnabled = true

        autoRefreshJob = viewModelScope.launch {
            while (isActive && isAutoRefreshEnabled) {
                loadAssets()
                delay(15000) // 15초 갱신
            }
        }
    }

    fun stopAutoRefresh() {
        isAutoRefreshEnabled = false
        autoRefreshJob?.cancel()
    }

    /**
     * 자산 데이터 로드
     * UseCase에 모든 데이터 조회/계산 위임
     */
    private fun loadAssets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getAllHoldingsUseCase(minValue = 1.0)
                .onSuccess { result ->
                    val totalChange = result.allHoldings.sumOf { it.change }
                    val totalBuyValue = result.totalValue - totalChange
                    val totalChangeRate = if (totalBuyValue > 0) {
                        (totalChange / totalBuyValue) * 100
                    } else 0.0

                    _uiState.value = _uiState.value.copy(
                        totalValue = result.totalValue,
                        totalChange = totalChange,
                        totalChangeRate = totalChangeRate,
                        topAggregatedHoldings = result.aggregatedHoldings.take(5),
                        allHoldings = result.allHoldings,
                        exchangeBreakdown = result.exchangeResults.map { it.exchangeData },
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                    android.util.Log.e("AssetsOverview", "Error loading assets", e)
                }
        }
    }
    /**
     * 자동 갱신 시작
     * 모든 api 조회 실패시 설정 페이지로 이동
     * 설정페이지에서 거래소 연동 재시도
     */



    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}