package com.crypto.cryptoview.presentation.component.assetsOverview.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.local.DisplayCurrencyManager
import com.crypto.cryptoview.domain.model.ai.AiPortfolioInsightStage
import com.crypto.cryptoview.domain.usecase.ai.GenerateAiPortfolioInsightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AiPortfolioInsightViewModel @Inject constructor(
    private val generateAiPortfolioInsightUseCase: GenerateAiPortfolioInsightUseCase,
    private val displayCurrencyManager: DisplayCurrencyManager
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
            val displayCurrency = displayCurrencyManager.displayCurrencyFlow.first()

            generateAiPortfolioInsightUseCase(displayCurrency) { stage ->
                _uiState.value = when (stage) {
                    AiPortfolioInsightStage.REFRESHING_ASSETS -> AiPortfolioInsightUiState.RefreshingAssets
                    AiPortfolioInsightStage.GENERATING_INSIGHT -> AiPortfolioInsightUiState.GeneratingInsight
                }
            }.onSuccess { insight ->
                _uiState.value = AiPortfolioInsightUiState.Success(
                    insight = insight,
                    insightParagraphs = insight.insight.toReadableParagraphs()
                )
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

    private fun String.toReadableParagraphs(): List<String> {
        val normalized = trim()
            .replace("\r\n", "\n")
            .replace(Regex("[ \t]+"), " ")

        if (normalized.isBlank()) return emptyList()

        val paragraphSplit = normalized
            .replace(Regex("\\n{2,}"), "\n\n")
            .split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (paragraphSplit.size > 1) {
            return paragraphSplit.flatMap { it.splitReadableLines() }
        }

        return normalized
            .replace(Regex("\\s+(?=\\d+[.)]\\s)"), "\n\n")
            .replace(Regex("\\s+(?=-\\s)"), "\n\n")
            .replace(Regex("(?<=[.!?。])\\s+(?=[가-힣A-Za-z0-9])"), "\n\n")
            .split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mergeShortParagraphs()
    }

    private fun String.splitReadableLines(): List<String> {
        return lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .flatMap { line ->
                line
                    .replace(Regex("\\s+(?=\\d+[.)]\\s)"), "\n")
                    .replace(Regex("\\s+(?=-\\s)"), "\n")
                    .split("\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            }
    }

    private fun List<String>.mergeShortParagraphs(): List<String> {
        val result = mutableListOf<String>()
        var buffer = ""

        forEach { paragraph ->
            buffer = if (buffer.isBlank()) paragraph else "$buffer $paragraph"

            if (buffer.length >= 70 || paragraph.endsWith(":")) {
                result += buffer
                buffer = ""
            }
        }

        if (buffer.isNotBlank()) result += buffer
        return result
    }
}

