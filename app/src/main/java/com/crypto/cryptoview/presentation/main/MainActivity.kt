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
import com.crypto.cryptoview.presentation.login.LoginViewModel
import com.crypto.cryptoview.ui.theme.CryptoViewTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    AppNavigation(onLogout = { finishAffinity() })
                }
            }
        }
    }
}

@Composable
fun AppNavigation(onLogout: () -> Unit = {}) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    // 저장된 인증 정보 확인
    LaunchedEffect(Unit) {
        scope.launch {
            isLoggedIn = loginViewModel.hasAnyCredentials()
        }
    }

    when (isLoggedIn) {
        null -> {
            // 로딩 중
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // 로딩 화면 (선택사항)
            }
        }
        false -> {
            // 로그인 화면
            LoginScreen(
                onLoginSuccess = { isLoggedIn = true }
            )
        }
        true -> {
            // 메인 화면
            val viewModel: AssetsOverviewViewModel = hiltViewModel()
            val holdingCoinsViewModel: HoldingCoinsViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel,
                holdingsViewModel = holdingCoinsViewModel,
                onLogout = onLogout
            )
        }
    }
}
