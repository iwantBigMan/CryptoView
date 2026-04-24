package com.crypto.cryptoview.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.presentation.component.assetsOverview.AssetsOverviewViewModel
import com.crypto.cryptoview.presentation.component.holdingCoinView.HoldingCoinsViewModel
import com.crypto.cryptoview.presentation.login.LoginScreen
import com.crypto.cryptoview.presentation.login.GoogleLoginViewModel
import com.crypto.cryptoview.presentation.settings.ExchangeSettingsViewModel
import com.crypto.cryptoview.ui.theme.CryptoViewTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        enableEdgeToEdge()
        setContent {
            CryptoViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(onLogout = { finishAffinity() })
                }
            }
        }
    }
}

/**
 * 앱 인증 상태
 */
enum class AuthState {
    LOADING,
    NEED_GOOGLE_LOGIN,
    NEED_EXCHANGE,
    READY
}

@Composable
fun AppNavigation(
    onLogout: () -> Unit = {}
) {
    val exchangeSettingsViewModel: ExchangeSettingsViewModel = hiltViewModel()
    val googleLoginViewModel: GoogleLoginViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    var authState by remember { mutableStateOf(AuthState.LOADING) }

    // 인증 상태 확인 — ViewModel을 통해 (클린 아키텍처)
    LaunchedEffect(Unit) {
        authState = when {
            !googleLoginViewModel.isSignedIn() -> AuthState.NEED_GOOGLE_LOGIN
            !exchangeSettingsViewModel.hasAnyCredentials() -> AuthState.NEED_EXCHANGE
            else -> AuthState.READY
        }
    }

    when (authState) {
        AuthState.LOADING -> {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) { /* 로딩 */ }
        }

        AuthState.NEED_GOOGLE_LOGIN -> {
            LoginScreen(
                viewModel = googleLoginViewModel,
                onLoginSuccess = {
                    scope.launch {
                        authState = if (exchangeSettingsViewModel.hasAnyCredentials()) {
                            AuthState.READY
                        } else {
                            AuthState.NEED_EXCHANGE
                        }
                    }
                }
            )
        }

        AuthState.NEED_EXCHANGE -> {
            val viewModel: AssetsOverviewViewModel = hiltViewModel()
            val holdingCoinsViewModel: HoldingCoinsViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel,
                holdingsViewModel = holdingCoinsViewModel,
                initialTab = 2,
                showExchangeSetup = true,
                onLogout = {
                    scope.launch {
                        googleLoginViewModel.signOut()
                        exchangeSettingsViewModel.logout()
                        authState = AuthState.NEED_GOOGLE_LOGIN
                    }
                },
                onExchangeLinked = {
                    authState = AuthState.READY
                }
            )
        }

        AuthState.READY -> {
            val viewModel: AssetsOverviewViewModel = hiltViewModel()
            val holdingCoinsViewModel: HoldingCoinsViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel,
                holdingsViewModel = holdingCoinsViewModel,
                onLogout = {
                    scope.launch {
                        googleLoginViewModel.signOut()
                        exchangeSettingsViewModel.logout()
                        authState = AuthState.NEED_GOOGLE_LOGIN
                    }
                }
            )
        }
    }
}
