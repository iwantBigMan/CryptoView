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

/**
 * 거래소 인증 정보 저장소
 * DataStore를 사용하여 안전하게 API 키를 저장 (암호화 적용)
 */
@Singleton
class CredentialsManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val UPBIT_API_KEY = stringPreferencesKey("upbit_api_key")
        private val UPBIT_SECRET_KEY = stringPreferencesKey("upbit_secret_key")
        private val UPBIT_LINKED = booleanPreferencesKey("upbit_linked")
        private val GATEIO_API_KEY = stringPreferencesKey("gateio_api_key")
        private val GATEIO_SECRET_KEY = stringPreferencesKey("gateio_secret_key")
        private val BINANCE_API_KEY = stringPreferencesKey("binance_api_key")
        private val BINANCE_SECRET_KEY = stringPreferencesKey("binance_secret_key")
        private val BYBIT_API_KEY = stringPreferencesKey("bybit_api_key")
        private val BYBIT_SECRET_KEY = stringPreferencesKey("bybit_secret_key")
    }

    /**
     * 저장된 인증 정보를 Flow로 제공 (복호화 수행)
     */
    val credentials: Flow<ExchangeCredentials> = dataStore.data.map { preferences ->
        val upbitApiEnc = preferences[UPBIT_API_KEY]
        val upbitSecretEnc = preferences[UPBIT_SECRET_KEY]
        val gateApiEnc = preferences[GATEIO_API_KEY]
        val gateSecretEnc = preferences[GATEIO_SECRET_KEY]
        val binanceApiEnc = preferences[BINANCE_API_KEY]
        val binanceSecretEnc = preferences[BINANCE_SECRET_KEY]
        val bybitApiEnc = preferences[BYBIT_API_KEY]
        val bybitSecretEnc = preferences[BYBIT_SECRET_KEY]

        ExchangeCredentials(
            upbitApiKey = upbitApiEnc?.let { SecureStorage.decrypt(it) } ?: "",
            upbitSecretKey = upbitSecretEnc?.let { SecureStorage.decrypt(it) } ?: "",
            gateioApiKey = gateApiEnc?.let { SecureStorage.decrypt(it) } ?: "",
            gateioSecretKey = gateSecretEnc?.let { SecureStorage.decrypt(it) } ?: "",
            binanceApiKey = binanceApiEnc?.let { SecureStorage.decrypt(it) } ?: "",
            binanceSecretKey = binanceSecretEnc?.let { SecureStorage.decrypt(it) } ?: "",
            bybitApiKey = bybitApiEnc?.let { SecureStorage.decrypt(it) } ?: "",
            bybitSecretKey = bybitSecretEnc?.let { SecureStorage.decrypt(it) } ?: ""
        )
    }



    suspend fun markUpbitCredentialsLinked() {
        dataStore.edit { preferences ->
            preferences[UPBIT_LINKED] = true
        }
    }

    suspend fun hasUpbitCredentialsLinked(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[UPBIT_LINKED] ?: false
        }.first()
    }

    /**
     * Gate.io 인증 정보 저장
     */
    suspend fun saveGateioCredentials(apiKey: String, secretKey: String) {
        val apiEnc = SecureStorage.encrypt(apiKey) ?: ""
        val secretEnc = SecureStorage.encrypt(secretKey) ?: ""
        dataStore.edit { preferences ->
            preferences[GATEIO_API_KEY] = apiEnc
            preferences[GATEIO_SECRET_KEY] = secretEnc
        }
    }

    /**
     * 바이낸스 인증 정보 저장
     */
    suspend fun saveBinanceCredentials(apiKey: String, secretKey: String) {
        val apiEnc = SecureStorage.encrypt(apiKey) ?: ""
        val secretEnc = SecureStorage.encrypt(secretKey) ?: ""
        dataStore.edit { preferences ->
            preferences[BINANCE_API_KEY] = apiEnc
            preferences[BINANCE_SECRET_KEY] = secretEnc
        }
    }

    /**
     * 바이비트 인증 정보 저장
     */
    suspend fun saveBybitCredentials(apiKey: String, secretKey: String) {
        val apiEnc = SecureStorage.encrypt(apiKey) ?: ""
        val secretEnc = SecureStorage.encrypt(secretKey) ?: ""
        dataStore.edit { preferences ->
            preferences[BYBIT_API_KEY] = apiEnc
            preferences[BYBIT_SECRET_KEY] = secretEnc
        }
    }

    /**
     * 모든 인증 정보 삭제 (로그아웃)
     */
    suspend fun clearAllCredentials() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearUpbitCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(UPBIT_API_KEY)
            preferences.remove(UPBIT_SECRET_KEY)
            preferences.remove(UPBIT_LINKED)
        }
    }

    suspend fun clearGateioCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(GATEIO_API_KEY)
            preferences.remove(GATEIO_SECRET_KEY)
        }
    }
}
