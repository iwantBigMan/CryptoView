package com.crypto.cryptoview.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.displayCurrencyDataStore by preferencesDataStore(name = "display_currency")

@Singleton
class DisplayCurrencyManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val DISPLAY_CURRENCY_KEY = stringPreferencesKey("display_currency")

    val displayCurrencyFlow: Flow<DisplayCurrency> = context.displayCurrencyDataStore.data.map { prefs ->
        when (prefs[DISPLAY_CURRENCY_KEY]) {
            DisplayCurrency.USDT.name -> DisplayCurrency.USDT
            else -> DisplayCurrency.KRW
        }
    }

    suspend fun setDisplayCurrency(currency: DisplayCurrency) {
        context.displayCurrencyDataStore.edit { prefs ->
            prefs[DISPLAY_CURRENCY_KEY] = currency.name
        }
    }
}
