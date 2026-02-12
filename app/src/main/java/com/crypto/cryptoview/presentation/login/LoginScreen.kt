package com.crypto.cryptoview.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.ui.theme.*

// 화면 전반에서 사용하는 구분선/테두리 색상(통일용)
private val DividerColor = Color(0xFF2A2D3E)

/**
 * 로그인/인증 정보 관리 화면
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // uiState 관찰 (네비게이션은 하단의 LaunchedEffect 하나에서 처리)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = BackgroundPrimary
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더
            item {
                Text(
                    text = "거래소 연동",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // 저장된 거래소 목록
            if (uiState.savedCredentials.isNotEmpty()) {

                item {
                    Text(
                        text = "연동된 거래소",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(items = uiState.savedCredentials) { exchange ->
                    SavedExchangeItem(
                        exchange = exchange,
                        onDelete = { viewModel.deleteCredentials(exchange) }
                    )
                }

                item {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = DividerColor,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // 로그아웃 버튼 제거 (설정 페이지로 이동)
            }

            // 업비트 입력 카드 (항상 표시, 필수)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ExchangeType.UPBIT.displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // 작은 '필수' 라벨
                            Text(
                                text = "필수",
                                color = Color(0xFFFFE6CC),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        ApiKeyInputField(
                            value = uiState.inputs[ExchangeType.UPBIT]?.apiKey ?: "",
                            onValueChange = { viewModel.updateApiKey(ExchangeType.UPBIT, it) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SecretKeyInputField(
                            value = uiState.inputs[ExchangeType.UPBIT]?.secretKey ?: "",
                            onValueChange = { viewModel.updateSecretKey(ExchangeType.UPBIT, it) }
                        )
                    }
                }
            }

            // 해외 거래소 드롭다운 (UPBIT 제외)
            item {
                var expanded by remember { mutableStateOf(false) }
                val others = ExchangeType.entries.filter { it != ExchangeType.UPBIT }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CardBackground,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "해외 거래소 선택")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "선택: ${uiState.selectedExchanges.joinToString { it.displayName }}",
                            color = TextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(300.dp)
                            .background(CardBackground)
                            .border(1.dp, CardBackground) // 테두리도 같은 색으로
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardBackground)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                others.forEach { ex ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(
                                                    checked = uiState.selectedExchanges.contains(ex),
                                                    onCheckedChange = null,
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = AccentBlue,
                                                        uncheckedColor = Color(0xFF6E7B86),
                                                        checkmarkColor = Color.White
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = ex.displayName, color = Color.White)
                                            }
                                        },
                                        onClick = {
                                            viewModel.toggleExchangeSelection(ex)
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = Color.White
                                        ),
                                        modifier = Modifier.background(CardBackground)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 선택된 해외 거래소들에 대한 입력 필드 표시
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    uiState.selectedExchanges.toList().forEach { ex ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = ex.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                ApiKeyInputField(
                                    value = uiState.inputs[ex]?.apiKey ?: "",
                                    onValueChange = { viewModel.updateApiKey(ex, it) }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                SecretKeyInputField(
                                    value = uiState.inputs[ex]?.secretKey ?: "",
                                    onValueChange = { viewModel.updateSecretKey(ex, it) }
                                )
                            }
                        }
                    }
                }
            }

            // 저장 버튼 (선택된 모든 거래소 저장)
            item {
                Button(onClick = { viewModel.saveSelectedCredentials() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) {
                    Text("연동하기")
                }
            }

            // 안내 메시지
            item {
                InfoCard()
            }
        }
    }

    // show error dialog if present (outside LazyColumn so it's on top)
    val error = uiState.error
    LoginErrorDialog(errorMessage = error, onDismiss = { viewModel.clearError() })

    // Observe loginSuccess to navigate and then clear flag
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            try {
                onLoginSuccess()
            } catch (t: Throwable) {
                android.util.Log.e("LoginScreen", "navigation failed", t)
            } finally {
                viewModel.clearLoginSuccess()
            }
        }
    }

}

/**
 * API Key 입력 필드
 */
@Composable
private fun ApiKeyInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = "API Key",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("API Key를 입력하세요", color = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
    }
}

/**
 * Secret Key 입력 필드 (비밀번호 형식)
 */
@Composable
private fun SecretKeyInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Secret Key",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Secret Key를 입력하세요", color = TextSecondary) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide" else "Show",
                        tint = TextSecondary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}

/**
 * 저장된 거래소 아이템
 */
@Composable
private fun SavedExchangeItem(
    exchange: ExchangeType,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = exchange.displayName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "연동됨",
                    color = PositiveColor,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = ErrorColor
                )
            }
        }
    }
}

/**
 * 안내 카드
 */
@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D2E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📌 필수 안내",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• 업비트 연동은 필수입니다 (USDT/KRW 환율 조회용)\n" +
                      "• 해외 거래소는 선택사항입니다\n" +
                      "• API Key는 거래소 웹사이트에서 발급받을 수 있습니다\n" +
                      "• 출금 권한은 체크하지 마세요\n" +
                      "• API Key는 암호화되어 기기에만 저장됩니다",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * API Key/Secret Key 입력 실패 시 보여줄 오류 다이얼로그
 */
@Composable
private fun LoginErrorDialog(errorMessage: String?, onDismiss: () -> Unit) {
    if (errorMessage == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("확인") }
        },
        title = { Text("연동 실패", color = Color.White) },
        text = { Text(errorMessage, color = Color.White) },
        containerColor = Color(0xFF1A1D2E)
    )
}
