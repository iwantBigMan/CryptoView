package com.crypto.cryptoview.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.local.CredentialsManager
import com.crypto.cryptoview.data.local.CredentialsProvider
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
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
 * 로그인 화면 ViewModel
 * 거래소별 API Key 관리 (다중 연동 지원)
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialsManager: CredentialsManager,
    private val credentialsProvider: CredentialsProvider,
    private val validateUpbit: ValidateUpbitCredentialsUseCase,
    private val validateGate: ValidateGateCredentialsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        // collectLatest로 최신 값만 반영하고, 예외가 발생하면 UI에 에러를 남겨 크래시를 방지합니다.
        viewModelScope.launch {
            try {
                credentialsManager.credentials.collectLatest { credentials ->
                    val savedExchanges = mutableListOf<ExchangeType>()
                    if (credentials.hasUpbitCredentials()) savedExchanges.add(ExchangeType.UPBIT)
                    if (credentials.hasGateioCredentials()) savedExchanges.add(ExchangeType.GATEIO)
                    if (credentials.hasBinanceCredentials()) savedExchanges.add(ExchangeType.BINANCE)
                    if (credentials.hasBybitCredentials()) savedExchanges.add(ExchangeType.BYBIT)

                    // 기존 입력값이 있으면 유지하고, 없으면 빈 ExchangeInput으로 초기화합니다.
                    val inputs = ExchangeType.entries.associateWith { ex ->
                        _uiState.value.inputs[ex] ?: ExchangeInput()
                    }

                    _uiState.value = _uiState.value.copy(
                        savedCredentials = savedExchanges,
                        inputs = inputs
                    )
                }
            } catch (t: Throwable) {
                // DataStore 또는 복호화 과정에서 예외가 발생할 수 있으므로 UI에 에러만 표시하고 앱 크래시는 방지합니다.
                android.util.Log.e("LoginViewModel", "loadSavedCredentials error", t)
                _uiState.value = _uiState.value.copy(error = "인증 정보 로드 중 오류가 발생했습니다")
            }
        }
    }

    // 드롭다운에서 거래소 선택을 토글합니다 (추가/제거)
    fun toggleExchangeSelection(exchange: ExchangeType) {
        val current = _uiState.value.selectedExchanges.toMutableSet()
        if (current.contains(exchange)) current.remove(exchange) else current.add(exchange)
        _uiState.value = _uiState.value.copy(selectedExchanges = current)
        android.util.Log.d("LoginViewModel", "toggleExchangeSelection -> ${exchange.name}, selected=${current}")
    }

    // 거래소별 API Key 업데이트
    fun updateApiKey(exchange: ExchangeType, apiKey: String) {
        val current = _uiState.value.inputs.toMutableMap()
        val existing = current[exchange] ?: ExchangeInput()
        current[exchange] = existing.copy(apiKey = apiKey)
        _uiState.value = _uiState.value.copy(inputs = current)
    }

    // 거래소별 Secret Key 업데이트
    fun updateSecretKey(exchange: ExchangeType, secretKey: String) {
        val current = _uiState.value.inputs.toMutableMap()
        val existing = current[exchange] ?: ExchangeInput()
        current[exchange] = existing.copy(secretKey = secretKey)
        _uiState.value = _uiState.value.copy(inputs = current)
    }

    // 선택된 거래소들의 인증 정보를 일괄 저장합니다
    fun saveSelectedCredentials() {
        // 저장 시 항상 UPBIT을 포함합니다(업비트는 환율 조회용으로 필수).
        // UI 상태의 최신 값을 사용하여 stale 상태를 방지합니다.
        val currentState = _uiState.value
        val toSave = currentState.selectedExchanges.toMutableSet().apply { add(ExchangeType.UPBIT) }

        android.util.Log.d("LoginViewModel", "saveSelectedCredentials -> toSave=${toSave}")

        // 각 거래소에 API Key/Secret 입력이 있는지 검증합니다.
        for (ex in toSave) {
            val input = currentState.inputs[ex]
            if (input == null || input.apiKey.isBlank() || input.secretKey.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "${ex.displayName}의 API Key/Secret을 입력해주세요")
                return
            }
        }

        viewModelScope.launch {
            // 최신 상태로 로딩 표시를 설정합니다.
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // 먼저 검증: 입력된 키로 API 호출을 시도하여 연동 성공 여부를 확인합니다.
                // DataStore를 건드리지 않고 검증만 수행하여 실패 시 기존 저장된 키는 그대로 유지됩니다.
                for (ex in toSave) {
                    val input = _uiState.value.inputs[ex] ?: ExchangeInput()
                    val valid = when (ex) {
                        ExchangeType.UPBIT -> validateUpbit(input.apiKey, input.secretKey)
                        ExchangeType.GATEIO -> validateGate(input.apiKey, input.secretKey)
                        // TODO: 바이낸스/바이빗 검증 로직 추가 필요 시 여기에 구현
                        else -> true
                    }

                    if (!valid) {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "${ex.displayName} 연동 검증 실패. API Key/Secret을 확인하세요.")
                        return@launch
                    }
                }

                // 모든 검증 성공: 기존 저장된 인증정보를 제거하고 새로 저장합니다.
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

                // 모든 검증 및 저장 성공: UI 상태 업데이트 및 선택 해제
                _uiState.value = _uiState.value.copy(isLoading = false, selectedExchanges = emptySet(), loginSuccess = true)
            } catch (e: Throwable) {
                android.util.Log.e("LoginViewModel", "saveSelectedCredentials error", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "저장 실패: ${e.message ?: e::class.simpleName}")
            }
        }
    }

    // 특정 거래소의 인증 정보를 삭제합니다.
    fun deleteCredentials(exchange: ExchangeType) {
        viewModelScope.launch {
            try {
                when (exchange) {
                    ExchangeType.UPBIT -> credentialsManager.clearUpbitCredentials()
                    ExchangeType.GATEIO -> credentialsManager.clearGateioCredentials()
                    else -> {}
                }
            } catch (t: Throwable) {
                android.util.Log.e("LoginViewModel", "deleteCredentials error", t)
                _uiState.value = _uiState.value.copy(error = "삭제 중 오류가 발생했습니다")
            }
        }
    }

    // 모든 인증 정보를 삭제합니다.
    fun clearAllCredentials() {
        viewModelScope.launch { credentialsManager.clearAllCredentials() }
    }

    /**
     * 완전 로그아웃: 저장소 + 메모리 캐시 모두 정리
     */
    fun logout() {
        viewModelScope.launch {
            // 1. DataStore에서 모든 인증 정보 삭제
            credentialsManager.clearAllCredentials()
            // 2. 메모리 캐시 초기화 (CredentialsProvider)
            credentialsProvider.clear()
            // 3. UI 상태 초기화 (입력 필드도 빈 값으로 강제 설정)
            _uiState.value = LoginUiState(
                inputs = ExchangeType.entries.associateWith { ExchangeInput() } // 빈 입력 필드 강제 설정
            )
        }
    }

    // 에러 메시지를 초기화합니다.
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // UI에서 네비게이션이 완료되면 호출하여 로그인 성공 플래그를 초기화합니다.
    fun clearLoginSuccess() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }

    // 저장된 인증 정보 존재 여부를 확인합니다.
    suspend fun hasAnyCredentials(): Boolean {
        return credentialsManager.credentials.first().hasRequiredCredentials()
    }
}
