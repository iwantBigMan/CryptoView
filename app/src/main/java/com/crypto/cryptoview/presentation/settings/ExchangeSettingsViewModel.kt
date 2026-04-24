package com.crypto.cryptoview.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.local.CredentialsManager
import com.crypto.cryptoview.data.local.CredentialsProvider
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.usecase.auth.ValidateAndSaveUpbitCredentialsUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 거래소 연동 설정 ViewModel
 * 백엔드를 통한 키 검증 + 로컬 기기 저장
 */
@HiltViewModel
class ExchangeSettingsViewModel @Inject constructor(
    private val credentialsManager: CredentialsManager,
    private val credentialsProvider: CredentialsProvider,
    private val saveUpbit: ValidateAndSaveUpbitCredentialsUseCase,
    private val getUpbitAccountBalances: GetUpbitAccountBalancesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeSettingsUiState())
    val uiState: StateFlow<ExchangeSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val savedExchanges = mutableListOf<ExchangeType>()

                // 백엔드 API 호출 성공 여부로 업비트 연동 상태 판단
                getUpbitAccountBalances()
                    .onSuccess { savedExchanges.add(ExchangeType.UPBIT) }
                    .onFailure { android.util.Log.d("ExchangeSettingsVM", "업비트 미연동: ${it.message}") }

                // Gate.io는 로컬 키 존재 여부로 판단
                credentialsManager.credentials.first().let { creds ->
                    if (creds.hasGateioCredentials()) savedExchanges.add(ExchangeType.GATEIO)
                }

                val inputs = ExchangeType.entries.associateWith { ex ->
                    _uiState.value.inputs[ex] ?: ExchangeInput()
                }

                _uiState.value = _uiState.value.copy(
                    savedCredentials = savedExchanges,
                    inputs = inputs,
                    isLoading = false
                )
            } catch (t: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "loadSavedCredentials error", t)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "연동 상태 확인 중 오류가 발생했습니다"
                )
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

    /** 선택된 거래소들의 인증 정보를 백엔드 검증 후 로컬 저장 */
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
                    when (ex) {
                        ExchangeType.UPBIT -> {
                            // 검증 성공 → 백엔드에 저장
                            val saveResponse = saveUpbit(input.apiKey, input.secretKey)
                            if (saveResponse.saved != true) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = saveResponse.message
                                )
                                return@launch
                            }
                        }
                        else -> {
                            // 다른 거래소는 추후 백엔드 구현 시 추가
                        }
                    }
                }


                _uiState.value = _uiState.value.copy(isLoading = false, selectedExchanges = emptySet(), saveSuccess = true)
                loadSavedCredentials() // 연동 성공 후 상태 즉시 갱신
            } catch (e: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "saveSelectedCredentials error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "검증 실패: ${e.message ?: e::class.simpleName}"
                )
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

    /** 연동된 거래소 존재 여부 확인 — 백엔드 API 호출로 판단 */
    suspend fun hasAnyCredentials(): Boolean {
        return getUpbitAccountBalances().isSuccess
    }
}
