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
import com.crypto.cryptoview.data.auth.GoogleAuthService
import com.crypto.cryptoview.presentation.component.assetsOverview.AssetsOverviewViewModel
import com.crypto.cryptoview.presentation.component.holdingCoinView.HoldingCoinsViewModel
import com.crypto.cryptoview.presentation.login.GoogleLoginScreen
import com.crypto.cryptoview.presentation.login.GoogleLoginViewModel
import com.crypto.cryptoview.presentation.login.LoginViewModel
import com.crypto.cryptoview.ui.theme.CryptoViewTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthService: GoogleAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-Edge 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Status Bar 아이콘 색상 설정 (흰색)
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
                    AppNavigation(
                        googleAuthService = googleAuthService,
                        onLogout = { finishAffinity() }
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
    LOADING,          // 확인 중
    NEED_GOOGLE_LOGIN, // 구글 로그인 필요
    NEED_EXCHANGE,    // 로그인됨, 거래소 연동 필요 (업비트 필수)
    READY             // 모두 완료 → 메인 화면
}

@Composable
fun AppNavigation(
    googleAuthService: GoogleAuthService,
    onLogout: () -> Unit = {}
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val googleLoginViewModel: GoogleLoginViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    var authState by remember { mutableStateOf(AuthState.LOADING) }

    // 인증 상태 확인
    LaunchedEffect(Unit) {
        scope.launch {
            authState = when {
                // 1. Google 로그인 안 됨
                !googleAuthService.isSignedIn -> AuthState.NEED_GOOGLE_LOGIN
                // 2. Google 로그인 됨 + 업비트 연동 안 됨
                !loginViewModel.hasAnyCredentials() -> AuthState.NEED_EXCHANGE
                // 3. 모두 완료
                else -> AuthState.READY
            }
        }
    }

    when (authState) {
        AuthState.LOADING -> {
            // 스플래시/로딩
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) { /* 로딩 */ }
        }

        AuthState.NEED_GOOGLE_LOGIN -> {
            GoogleLoginScreen(
                viewModel = googleLoginViewModel,
                onLoginSuccess = {
                    // Google 로그인 성공 → 거래소 연동 확인
                    scope.launch {
                        authState = if (loginViewModel.hasAnyCredentials()) {
                            AuthState.READY
                        } else {
                            AuthState.NEED_EXCHANGE
                        }
                    }
                }
            )
        }

        AuthState.NEED_EXCHANGE -> {
            // 메인 화면으로 이동하되, Settings 탭 강제 + 연동 다이얼로그 표시
            val viewModel: AssetsOverviewViewModel = hiltViewModel()
            val holdingCoinsViewModel: HoldingCoinsViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel,
                holdingsViewModel = holdingCoinsViewModel,
                initialTab = 2, // Settings 탭으로 강제 이동
                showExchangeSetup = true, // 거래소 연동 다이얼로그 표시
                onLogout = {
                    scope.launch {
                        googleLoginViewModel.signOut()
                        loginViewModel.logout()
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
                        loginViewModel.logout()
                        authState = AuthState.NEED_GOOGLE_LOGIN
                    }
                }
            )
        }
    }
}


