package com.crypto.cryptoview.presentation.component.holdingCoinView

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.usecase.GetAllHoldingsUseCase
import com.crypto.cryptoview.domain.usecase.GetExchangeHoldingDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 보유 상세 화면 ViewModel
 * 클린 아키텍처 준수: UseCase를 통해 비즈니스 로직 처리
 * ViewModel은 UI 상태 관리만 담당
 */
@HiltViewModel
class HoldingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAllHoldingsUseCase: GetAllHoldingsUseCase,
    private val getExchangeHoldingDetailsUseCase: GetExchangeHoldingDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingDetailUiState())
    val uiState: StateFlow<HoldingDetailUiState> = _uiState.asStateFlow()

    // 현재 심볼 (변경 가능)
    private var currentSymbol: String = savedStateHandle["symbol"] ?: ""

    init {
        if (currentSymbol.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(symbol = currentSymbol)
            loadData()
        }
    }

    /**
     * 데이터 로드
     * 1. GetAllHoldingsUseCase로 전체 보유 자산 조회
     * 2. GetExchangeHoldingDetailsUseCase로 해당 심볼의 거래소별 상세 조회
     */
    private fun loadData() {
        if (currentSymbol.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // 1. 전체 보유 자산 조회
                val holdingsResult = getAllHoldingsUseCase(minValue = 0.0)

                holdingsResult.onSuccess { result ->
                    // 2. 해당 심볼의 거래소별 상세 조회
                    val detailResult = getExchangeHoldingDetailsUseCase(
                        symbol = currentSymbol,
                        allHoldings = result.allHoldings,
                        usdtKrwRate = 1.0 // 이미 KRW로 변환된 값 사용
                    )

                    // UseCase 결과를 UI 상태로 매핑
                    _uiState.value = _uiState.value.copy(
                        symbol = detailResult.symbol,
                        coinName = detailResult.coinName,
                        totalValueKrw = detailResult.totalValueKrw,
                        totalProfitLoss = detailResult.totalProfitLoss,
                        totalProfitLossPercent = detailResult.totalProfitLossPercent,
                        exchangeHoldings = detailResult.exchangeHoldings,
                        isLoading = false,
                        error = null
                    )
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "데이터 로드 실패"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "알 수 없는 오류"
                )
            }
        }
    }

    /**
     * 심볼 설정 및 데이터 로드
     * Screen에서 호출됨
     */
    fun setSymbol(newSymbol: String) {
        if (newSymbol.isNotEmpty() && newSymbol != currentSymbol) {
            currentSymbol = newSymbol
            _uiState.value = _uiState.value.copy(symbol = newSymbol)
            loadData()
        } else if (newSymbol.isNotEmpty() && _uiState.value.exchangeHoldings.isEmpty() && !_uiState.value.isLoading) {
            // 심볼은 같지만 데이터가 없으면 다시 로드
            loadData()
        }
    }

    /**
     * 새로고침
     */
    fun refresh() {
        loadData()
    }
}
