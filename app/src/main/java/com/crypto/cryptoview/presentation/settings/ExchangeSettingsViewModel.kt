package com.crypto.cryptoview.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.local.CredentialsManager
import com.crypto.cryptoview.data.local.CredentialsProvider
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.usecase.auth.ValidateGateCredentialsUseCase
import com.crypto.cryptoview.domain.usecase.auth.ValidateUpbitCredentialsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 거래소 연동 설정 ViewModel
 * 거래소별 API Key 관리 (다중 연동 지원)
 * 설정 페이지에서 사용
 */
@HiltViewModel
class ExchangeSettingsViewModel @Inject constructor(
    private val credentialsManager: CredentialsManager,
    private val credentialsProvider: CredentialsProvider,
    private val validateUpbit: ValidateUpbitCredentialsUseCase,
    private val validateGate: ValidateGateCredentialsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeSettingsUiState())
    val uiState: StateFlow<ExchangeSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        viewModelScope.launch {
            try {
                credentialsManager.credentials.collectLatest { credentials ->
                    val savedExchanges = mutableListOf<ExchangeType>()
                    if (credentials.hasUpbitCredentials()) savedExchanges.add(ExchangeType.UPBIT)
                    if (credentials.hasGateioCredentials()) savedExchanges.add(ExchangeType.GATEIO)
                    if (credentials.hasBinanceCredentials()) savedExchanges.add(ExchangeType.BINANCE)
                    if (credentials.hasBybitCredentials()) savedExchanges.add(ExchangeType.BYBIT)

                    val inputs = ExchangeType.entries.associateWith { ex ->
                        _uiState.value.inputs[ex] ?: ExchangeInput()
                    }

                    _uiState.value = _uiState.value.copy(
                        savedCredentials = savedExchanges,
                        inputs = inputs
                    )
                }
            } catch (t: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "loadSavedCredentials error", t)
                _uiState.value = _uiState.value.copy(error = "인증 정보 로드 중 오류가 발생했습니다")
            }
        }
    }

    /** 거래소 선택 토글 (추가/제거) */
    fun toggleExchangeSelection(exchange: ExchangeType) {
        val current = _uiState.value.selectedExchanges.toMutableSet()
        if (current.contains(exchange)) current.remove(exchange) else current.add(exchange)
        _uiState.value = _uiState.value.copy(selectedExchanges = current)
    }

    /** 거래소별 API Key 업데이트 */
    fun updateApiKey(exchange: ExchangeType, apiKey: String) {
        val current = _uiState.value.inputs.toMutableMap()
        val existing = current[exchange] ?: ExchangeInput()
        current[exchange] = existing.copy(apiKey = apiKey)
        _uiState.value = _uiState.value.copy(inputs = current)
    }

    /** 거래소별 Secret Key 업데이트 */
    fun updateSecretKey(exchange: ExchangeType, secretKey: String) {
        val current = _uiState.value.inputs.toMutableMap()
        val existing = current[exchange] ?: ExchangeInput()
        current[exchange] = existing.copy(secretKey = secretKey)
        _uiState.value = _uiState.value.copy(inputs = current)
    }

    /** 선택된 거래소들의 인증 정보를 일괄 저장 */
    fun saveSelectedCredentials() {
        val currentState = _uiState.value
        val toSave = currentState.selectedExchanges.toMutableSet().apply { add(ExchangeType.UPBIT) }

        // 각 거래소에 API Key/Secret 입력이 있는지 검증
        for (ex in toSave) {
            val input = currentState.inputs[ex]
            if (input == null || input.apiKey.isBlank() || input.secretKey.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "${ex.displayName}의 API Key/Secret을 입력해주세요")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // 검증: 입력된 키로 API 호출하여 연동 성공 여부 확인
                for (ex in toSave) {
                    val input = _uiState.value.inputs[ex] ?: ExchangeInput()
                    val valid = when (ex) {
                        ExchangeType.UPBIT -> validateUpbit(input.apiKey, input.secretKey)
                        ExchangeType.GATEIO -> validateGate(input.apiKey, input.secretKey)
                        else -> true
                    }
                    if (!valid) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "${ex.displayName} 연동 검증 실패. API Key/Secret을 확인하세요."
                        )
                        return@launch
                    }
                }

                // 모든 검증 성공 → 저장
                credentialsManager.clearAllCredentials()
                val stateNow = _uiState.value
                for (ex in toSave) {
                    val input = stateNow.inputs[ex] ?: ExchangeInput()
                    when (ex) {
                        ExchangeType.UPBIT -> credentialsManager.saveUpbitCredentials(input.apiKey, input.secretKey)
                        ExchangeType.GATEIO -> credentialsManager.saveGateioCredentials(input.apiKey, input.secretKey)
                        ExchangeType.BINANCE -> credentialsManager.saveBinanceCredentials(input.apiKey, input.secretKey)
                        ExchangeType.BYBIT -> credentialsManager.saveBybitCredentials(input.apiKey, input.secretKey)
                    }
                }

                _uiState.value = _uiState.value.copy(isLoading = false, selectedExchanges = emptySet(), saveSuccess = true)
            } catch (e: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "saveSelectedCredentials error", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "저장 실패: ${e.message ?: e::class.simpleName}")
            }
        }
    }

    /** 특정 거래소 인증 정보 삭제 */
    fun deleteCredentials(exchange: ExchangeType) {
        viewModelScope.launch {
            try {
                when (exchange) {
                    ExchangeType.UPBIT -> credentialsManager.clearUpbitCredentials()
                    ExchangeType.GATEIO -> credentialsManager.clearGateioCredentials()
                    else -> {}
                }
            } catch (t: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "deleteCredentials error", t)
                _uiState.value = _uiState.value.copy(error = "삭제 중 오류가 발생했습니다")
            }
        }
    }

    /** 완전 로그아웃: 저장소 + 메모리 캐시 모두 정리 */
    fun logout() {
        viewModelScope.launch {
            credentialsManager.clearAllCredentials()
            credentialsProvider.clear()
            _uiState.value = ExchangeSettingsUiState(
                inputs = ExchangeType.entries.associateWith { ExchangeInput() }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    /** 저장된 인증 정보 존재 여부 확인 */
    suspend fun hasAnyCredentials(): Boolean {
        return credentialsManager.credentials.first().hasRequiredCredentials()
    }
}

