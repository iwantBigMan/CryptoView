package com.crypto.cryptoview.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.repository.ExchangeCredentialRepository
import com.crypto.cryptoview.domain.repository.GoogleAuthRepository
import com.crypto.cryptoview.domain.usecase.auth.DeleteExchangeCredentialUseCase
import com.crypto.cryptoview.domain.usecase.auth.ValidateAndSaveGateIoCredentialsUseCase
import com.crypto.cryptoview.domain.usecase.auth.ValidateAndSaveUpbitCredentialsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeSettingsViewModel @Inject constructor(
    private val exchangeCredentialRepository: ExchangeCredentialRepository,
    private val saveUpbit: ValidateAndSaveUpbitCredentialsUseCase,
    private val saveGateIo: ValidateAndSaveGateIoCredentialsUseCase,
    private val deleteExchangeCredential: DeleteExchangeCredentialUseCase,
    private val googleAuthRepository: GoogleAuthRepository
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
                val savedExchanges = exchangeCredentialRepository.getSavedExchanges()
                val localInputs = ExchangeType.entries.associateWith { exchange ->
                    _uiState.value.inputs[exchange] ?: ExchangeInput()
                }

                _uiState.value = _uiState.value.copy(
                    savedCredentials = savedExchanges,
                    inputs = localInputs,
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

    fun toggleExchangeSelection(exchange: ExchangeType) {
        val current = _uiState.value.selectedExchanges.toMutableSet()
        if (current.contains(exchange)) current.remove(exchange) else current.add(exchange)
        _uiState.value = _uiState.value.copy(selectedExchanges = current)
    }

    fun updateApiKey(exchange: ExchangeType, apiKey: String) {
        val current = _uiState.value.inputs.toMutableMap()
        val existing = current[exchange] ?: ExchangeInput()
        current[exchange] = existing.copy(apiKey = apiKey)
        _uiState.value = _uiState.value.copy(inputs = current)
    }

    fun updateSecretKey(exchange: ExchangeType, secretKey: String) {
        val current = _uiState.value.inputs.toMutableMap()
        val existing = current[exchange] ?: ExchangeInput()
        current[exchange] = existing.copy(secretKey = secretKey)
        _uiState.value = _uiState.value.copy(inputs = current)
    }

    fun saveCredential(exchange: ExchangeType, apiKey: String, secretKey: String) {
        _uiState.value = _uiState.value.copy(
            selectedExchanges = setOf(exchange),
            inputs = _uiState.value.inputs.toMutableMap().apply {
                put(exchange, ExchangeInput(apiKey = apiKey, secretKey = secretKey))
            }
        )
        saveSelectedCredentials()
    }

    fun saveSelectedCredentials() {
        val currentState = _uiState.value
        val toSave = currentState.selectedExchanges

        if (toSave.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "연동할 거래소를 선택하세요")
            return
        }

        for (exchange in toSave) {
            val input = currentState.inputs[exchange]
            if (input == null || input.apiKey.isBlank() || input.secretKey.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "${exchange.displayName} API Key/Secret을 입력하세요")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                for (exchange in toSave) {
                    val input = _uiState.value.inputs[exchange] ?: ExchangeInput()
                    when (exchange) {
                        ExchangeType.UPBIT -> {
                            val response = saveUpbit(input.apiKey, input.secretKey)
                            if (!response.saved) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = response.message
                                )
                                return@launch
                            }
                            exchangeCredentialRepository.markUpbitLinked()
                        }
                        ExchangeType.GATEIO -> {
                            val response = saveGateIo(input.apiKey, input.secretKey)
                            if (!response.saved) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = response.message
                                )
                                return@launch
                            }
                            exchangeCredentialRepository.markGateIoLinked()
                        }
                        else -> Unit
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedExchanges = emptySet(),
                    saveSuccess = true
                )
                loadSavedCredentials()
            } catch (e: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "saveSelectedCredentials error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "검증 실패: ${e.message ?: e::class.simpleName}"
                )
            }
        }
    }

    fun deleteCredentials(exchange: ExchangeType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                when (exchange) {
                    ExchangeType.UPBIT,
                    ExchangeType.GATEIO -> {
                        val response = deleteExchangeCredential(exchange)
                        android.util.Log.d(
                            "ExchangeSettingsVM",
                            "delete credential: ${response.deleted} ${response.message}"
                        )
                        if (!response.deleted) throw Exception("delete failed: ${response.message}")
                        exchangeCredentialRepository.clearCredentials(exchange)
                    }
                    else -> Unit
                }
                loadSavedCredentials()
            } catch (t: Throwable) {
                android.util.Log.e("ExchangeSettingsVM", "deleteCredentials error", t)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "삭제 중 오류가 발생했습니다"
                )
            }
        }
    }

    suspend fun logout() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        listOf(ExchangeType.UPBIT, ExchangeType.GATEIO).forEach { exchange ->
            try {
                val response = deleteExchangeCredential(exchange)
                android.util.Log.d(
                    "ExchangeSettingsVM",
                    "logout delete ${exchange.displayName}: ${response.deleted} ${response.message}"
                )
            } catch (t: Throwable) {
                android.util.Log.w("ExchangeSettingsVM", "logout delete ${exchange.displayName} failed", t)
            }
        }

        try {
            googleAuthRepository.signOut()
            if (googleAuthRepository.isSignedIn) {
                googleAuthRepository.signOut()
            }
        } catch (t: Throwable) {
            android.util.Log.e("ExchangeSettingsVM", "Google sign out failed", t)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Google 로그아웃에 실패했습니다: ${t.message ?: t::class.simpleName}"
            )
            throw Exception("Google 로그아웃 실패: ${t.message}", t)
        }

        try {
            exchangeCredentialRepository.clearAllCredentials()
            exchangeCredentialRepository.clearCache()
            _uiState.value = ExchangeSettingsUiState(
                inputs = ExchangeType.entries.associateWith { ExchangeInput() }
            )
        } catch (t: Throwable) {
            android.util.Log.e("ExchangeSettingsVM", "local credential clear failed", t)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    suspend fun hasAnyCredentials(): Boolean {
        return exchangeCredentialRepository.hasAnyCredentials()
    }
}
