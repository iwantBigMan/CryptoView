package com.crypto.cryptoview.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.crypto.cryptoview.domain.model.settings.AppTheme
import com.crypto.cryptoview.presentation.main.ThemeViewModel
import com.crypto.cryptoview.presentation.settings.ExchangeSettingsViewModel
import com.crypto.cryptoview.ui.theme.CryptoViewTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val theme by themeViewModel.currentTheme.collectAsState()
            val isDark = when (theme) {
                AppTheme.DARK   -> true
                AppTheme.LIGHT  -> false
                AppTheme.SYSTEM -> isSystemInDarkTheme()

            }

            // 다크모드: 상태바 아이콘 흰색 / 라이트모드: 상태바 아이콘 검정
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }

            CryptoViewTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onLogoutCompleted = {
                            finishAndRemoveTask()
                        }
                    )
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
    onLogoutCompleted: () -> Unit = {}
) {
    val exchangeSettingsViewModel: ExchangeSettingsViewModel = hiltViewModel()
    val googleLoginViewModel: GoogleLoginViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    var authState by remember { mutableStateOf(AuthState.LOADING) }
    val handleLogoutCompleted: () -> Unit = {
        googleLoginViewModel.markSignedOut()
        authState = AuthState.NEED_GOOGLE_LOGIN

        if (FirebaseAuth.getInstance().currentUser == null) {
            scope.launch {
                delay(300)
                onLogoutCompleted()
            }
        } else {
            Log.e("AppNavigation", "로그아웃 완료 후에도 Firebase currentUser가 남아 있습니다")
        }
    }

    // 인증 상태 확인 — ViewModel을 통해 (클린 아키텍처)
    LaunchedEffect(Unit) {
        // TODO: 테스트 후 삭제 ↓↓↓
        val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        Log.d("류류류", "Bearer $token")

        val t0 = System.currentTimeMillis()
        val result = exchangeSettingsViewModel.hasAnyCredentials()
        Log.d("류류류", "자산조회(hasAnyCredentials): ${System.currentTimeMillis() - t0}ms / result=$result")
        // TODO: 테스트 후 삭제 ↑↑↑

        authState = when {
            !googleLoginViewModel.isSignedIn() -> AuthState.NEED_GOOGLE_LOGIN
            !result -> AuthState.NEED_EXCHANGE
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
                onLogout = handleLogoutCompleted,
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
                onLogout = handleLogoutCompleted
            )
        }
    }
}
