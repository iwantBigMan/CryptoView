package com.crypto.cryptoview.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.repository.UpbitAssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val upbitRepository: UpbitAssetRepository
) : ViewModel() {

    private val _balances = MutableStateFlow<List<UpbitAccountBalance>>(emptyList())
    val balances: StateFlow<List<UpbitAccountBalance>> = _balances.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadBalances()
    }

    fun loadBalances() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            upbitRepository.getAccountBalances()
                .onSuccess { balances ->
                    _balances.value = balances
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                }

            _isLoading.value = false
        }
    }

    // 총 자산 가치 (KRW 환산)
    fun getTotalValue(): Double {
        return balances.value.sumOf { balance ->
            if (balance.currency == "KRW") {
                balance.balance
            } else {
                // 코인 가치 = 보유량 × 평균 매수가
                balance.balance * balance.avgBuyPrice
            }
        }
    }

    // KRW 잔고
    fun getKrwBalance(): Double {
        return balances.value
            .find { it.currency == "KRW" }
            ?.balance ?: 0.0
    }

    // 코인 총 가치
    fun getCryptoValue(): Double {
        return balances.value
            .filter { it.currency != "KRW" }
            .sumOf { it.balance * it.avgBuyPrice }
    }
}