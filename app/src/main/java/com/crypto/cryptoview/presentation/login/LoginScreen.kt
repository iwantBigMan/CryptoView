package com.crypto.cryptoview.presentation.login

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.R
import com.crypto.cryptoview.ui.theme.*

/**
 * 로그인 화면 (Google 로그인 전용)
 * 거래소 연동은 로그인 후 설정 페이지에서 처리
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: GoogleLoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 상태바 아이콘 흰색 (어두운 배경용)
    SideEffect {
        (context as? ComponentActivity)?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
    }

    // 로그인 성공 시 콜백
    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF071029),
                        Color(0xFF0D1B3E)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 앱 아이콘
            Text(text = "📊", fontSize = 72.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // 앱 이름
            Text(
                text = "CryptoView",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 부제목
            Text(
                text = "내 코인 자산을 한눈에",
                color = TextSecondary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 기능 소개
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureRow("📈", "실시간 자산 현황 조회(krw <-> usdt)")
                FeatureRow("🔗", "현재 업비트 · Gate.io 거래소 연동 가능")
                FeatureRow("🔒", "API 키 암호화 후 기기 저장")
                FeatureRow("☁️", "현재 Google 계정으로만 간편 로그인 가능")
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Google Sign-In 버튼 (Branding Guidelines 준수) ──
            Button(
                onClick = { viewModel.signInWithGoogle(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F1F1F),
                    disabledContainerColor = Color.White.copy(alpha = 0.7f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 1.dp,
                    pressedElevation = 0.dp
                ),
                contentPadding = PaddingValues(horizontal = 16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFF1F1F1F),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "로그인 중...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F1F1F)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google logo",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Unspecified // 원본 색상 유지
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 안내
            Text(
                text = "로그인 후 설정에서 거래소를 연동할 수 있습니다\nAPI 키는 서버에 저장되지 않습니다",
                color = TextTertiary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // 에러 다이얼로그
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("확인")
                }
            },
            title = { Text("로그인 실패", color = Color.White) },
            text = { Text(uiState.error ?: "", color = TextSecondary) },
            containerColor = CardBackground
        )
    }
}

@Composable
private fun FeatureRow(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 15.sp
        )
    }
}
