package com.crypto.cryptoview.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
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
