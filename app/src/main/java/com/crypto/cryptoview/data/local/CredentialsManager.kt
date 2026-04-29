package com.crypto.cryptoview.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crypto.cryptoview.domain.model.exchange.ExchangeCredentials
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "exchange_credentials")

@Singleton
class CredentialsManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val UPBIT_LINKED = booleanPreferencesKey("upbit_linked")
        private val GATEIO_LINKED = booleanPreferencesKey("gateio_linked")
        private val BINANCE_API_KEY = stringPreferencesKey("binance_api_key")
        private val BINANCE_SECRET_KEY = stringPreferencesKey("binance_secret_key")
        private val BYBIT_API_KEY = stringPreferencesKey("bybit_api_key")
        private val BYBIT_SECRET_KEY = stringPreferencesKey("bybit_secret_key")
    }

    val credentials: Flow<ExchangeCredentials> = dataStore.data.map { preferences ->
        val binanceApiEnc = preferences[BINANCE_API_KEY]
        val binanceSecretEnc = preferences[BINANCE_SECRET_KEY]
        val bybitApiEnc = preferences[BYBIT_API_KEY]
        val bybitSecretEnc = preferences[BYBIT_SECRET_KEY]

        ExchangeCredentials(
            binanceApiKey = binanceApiEnc?.let { SecureStorage.decrypt(it) } ?: "",
            binanceSecretKey = binanceSecretEnc?.let { SecureStorage.decrypt(it) } ?: "",
            bybitApiKey = bybitApiEnc?.let { SecureStorage.decrypt(it) } ?: "",
            bybitSecretKey = bybitSecretEnc?.let { SecureStorage.decrypt(it) } ?: ""
        )
    }

    suspend fun markUpbitLinked() {
        dataStore.edit { preferences ->
            preferences[UPBIT_LINKED] = true
        }
    }

    suspend fun hasUpbitLinked(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[UPBIT_LINKED] ?: false
        }.first()
    }

    suspend fun markGateIoLinked() {
        dataStore.edit { preferences ->
            preferences[GATEIO_LINKED] = true
        }
    }

    suspend fun hasGateIoLinked(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[GATEIO_LINKED] ?: false
        }.first()
    }

    suspend fun saveBinanceCredentials(apiKey: String, secretKey: String) {
        val apiEnc = SecureStorage.encrypt(apiKey) ?: ""
        val secretEnc = SecureStorage.encrypt(secretKey) ?: ""
        dataStore.edit { preferences ->
            preferences[BINANCE_API_KEY] = apiEnc
            preferences[BINANCE_SECRET_KEY] = secretEnc
        }
    }

    suspend fun saveBybitCredentials(apiKey: String, secretKey: String) {
        val apiEnc = SecureStorage.encrypt(apiKey) ?: ""
        val secretEnc = SecureStorage.encrypt(secretKey) ?: ""
        dataStore.edit { preferences ->
            preferences[BYBIT_API_KEY] = apiEnc
            preferences[BYBIT_SECRET_KEY] = secretEnc
        }
    }

    suspend fun clearAllCredentials() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearUpbitLinkStatus() {
        dataStore.edit { preferences ->
            preferences.remove(UPBIT_LINKED)
        }
    }

    suspend fun clearGateIoLinkStatus() {
        dataStore.edit { preferences ->
            preferences.remove(GATEIO_LINKED)
        }
    }
}
