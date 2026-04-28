package com.crypto.cryptoview.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crypto.cryptoview.domain.model.settings.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore by preferencesDataStore(name = "app_theme")

@Singleton
class ThemeManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val THEME_KEY = stringPreferencesKey("theme")

    val themeFlow: Flow<AppTheme> = context.themeDataStore.data.map { prefs ->
        when (prefs[THEME_KEY]) {
            AppTheme.DARK.name  -> AppTheme.DARK
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            else                -> AppTheme.SYSTEM
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.themeDataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}

