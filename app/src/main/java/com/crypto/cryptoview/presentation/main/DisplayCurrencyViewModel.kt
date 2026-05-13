package com.crypto.cryptoview.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.local.DisplayCurrencyManager
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayCurrencyViewModel @Inject constructor(
    private val displayCurrencyManager: DisplayCurrencyManager
) : ViewModel() {

    val currentCurrency: StateFlow<DisplayCurrency> = displayCurrencyManager.displayCurrencyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DisplayCurrency.KRW
        )

    fun setCurrency(currency: DisplayCurrency) {
        viewModelScope.launch {
            displayCurrencyManager.setDisplayCurrency(currency)
        }
    }
}
