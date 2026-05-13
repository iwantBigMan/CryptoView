package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsightStage
import com.crypto.cryptoview.domain.usecase.GenerateAiPortfolioInsightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AiPortfolioInsightViewModel @Inject constructor(
    private val generateAiPortfolioInsightUseCase: GenerateAiPortfolioInsightUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiPortfolioInsightUiState>(AiPortfolioInsightUiState.Idle)
    val uiState: StateFlow<AiPortfolioInsightUiState> = _uiState.asStateFlow()

    fun generateInsight() {
        if (_uiState.value is AiPortfolioInsightUiState.RefreshingAssets ||
            _uiState.value is AiPortfolioInsightUiState.GeneratingInsight
        ) {
            return
        }

        viewModelScope.launch {
            generateAiPortfolioInsightUseCase { stage ->
                _uiState.value = when (stage) {
                    AiPortfolioInsightStage.REFRESHING_ASSETS -> AiPortfolioInsightUiState.RefreshingAssets
                    AiPortfolioInsightStage.GENERATING_INSIGHT -> AiPortfolioInsightUiState.GeneratingInsight
                }
            }.onSuccess { insight ->
                _uiState.value = AiPortfolioInsightUiState.Success(insight)
            }.onFailure { throwable ->
                _uiState.value = AiPortfolioInsightUiState.Error(throwable.toUserMessage())
            }
        }
    }

    fun clearInsight() {
        _uiState.value = AiPortfolioInsightUiState.Idle
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is IOException -> "네트워크 연결을 확인한 뒤 다시 시도해 주세요."
            is HttpException -> when (code()) {
                401 -> "로그인이 만료되었습니다. 다시 로그인해 주세요."
                501 -> "AI 분석 기능이 아직 서버에서 준비되지 않았습니다."
                502 -> "AI 분석 서버 응답이 원활하지 않습니다. 잠시 후 다시 시도해 주세요."
                else -> "AI 포트폴리오 요약을 생성하지 못했습니다."
            }
            else -> message?.takeIf { it.isNotBlank() } ?: "AI 포트폴리오 요약을 생성하지 못했습니다."
        }
    }
}

