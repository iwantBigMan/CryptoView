package com.crypto.cryptoview.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.repository.AssetRepository
import com.crypto.cryptoview.model.AccountBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: AssetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadAccountBalances()
    }

    fun loadAccountBalances() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getAccountBalances()
                .onSuccess { balances ->
                    _uiState.value = UiState.Success(balances)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "알 수 없는 오류")
                }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val balances: List<AccountBalance>) : UiState
        data class Error(val message: String) : UiState
    }
}