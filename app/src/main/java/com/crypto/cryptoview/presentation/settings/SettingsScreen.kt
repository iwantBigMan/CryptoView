package com.crypto.cryptoview.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.presentation.login.GoogleLoginViewModel
import com.crypto.cryptoview.presentation.settings.ExchangeSettingsViewModel
import com.crypto.cryptoview.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: ExchangeSettingsViewModel = hiltViewModel(),
    googleLoginViewModel: GoogleLoginViewModel = hiltViewModel(),
    showExchangeSetup: Boolean = false,
    onLogout: () -> Unit = {},
    onExchangeLinked: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val googleUiState by googleLoginViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExchangeDialog by remember { mutableStateOf(showExchangeSetup) }

    Surface(
        modifier = modifier.fillMaxSize(),
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
                    text = "설정",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Google 계정 정보
            item {
                Text(
                    text = "계정",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = googleUiState.userName ?: "사용자",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = googleUiState.userEmail ?: "",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 연동된 계정 섹션
            item {
                Text(
                    text = "연동된 거래소",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 연동된 거래소 목록
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.savedCredentials.isEmpty()) {
                            Text(
                                text = "연동된 거래소가 없습니다",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        } else {
                            uiState.savedCredentials.forEach { exchange ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Key,
                                            contentDescription = null,
                                            tint = AccentBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = exchange.displayName,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Text(
                                        text = "연동됨",
                                        color = PositiveColor,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 거래소 연동 추가 버튼
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showExchangeDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "거래소 연동",
                            tint = AccentBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "거래소 연동 추가",
                            color = AccentBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 로그아웃 버튼
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "로그아웃",
                            tint = ErrorColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "로그아웃",
                            color = ErrorColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 앱 정보
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CryptoView",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Version 1.0.0",
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    // 로그아웃 확인 다이얼로그
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "로그아웃", color = Color.White)
            },
            text = {
                Text(
                    text = "Google 계정에서 로그아웃하고\n모든 연동된 거래소 정보가 삭제됩니다.\n계속하시겠습니까?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.logout()
                            googleLoginViewModel.signOut()
                            showLogoutDialog = false
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorColor)
                ) {
                    Text("로그아웃")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("취소")
                }
            },
            containerColor = CardBackground
        )
    }

    // 거래소 연동 다이얼로그
    if (showExchangeDialog) {
        ExchangeSetupDialog(
            isRequired = showExchangeSetup && uiState.savedCredentials.isEmpty(),
            viewModel = viewModel,
            onDismiss = {
                if (!showExchangeSetup || uiState.savedCredentials.isNotEmpty()) {
                    showExchangeDialog = false
                }
                // 필수 연동 상태에서는 닫기 불가
            },
            onSuccess = {
                showExchangeDialog = false
                onExchangeLinked()
            }
        )
    }
}

/**
 * 거래소 연동 다이얼로그
 * 업비트 API Key/Secret 입력
 */
@Composable
private fun ExchangeSetupDialog(
    isRequired: Boolean,
    viewModel: ExchangeSettingsViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var apiKey by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    // ViewModel의 검증 결과 관찰
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.clearSaveSuccess()
            onSuccess()
        }
    }

    // ViewModel 에러를 로컬 에러에 반영
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            localError = uiState.error
            viewModel.clearError()
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isRequired) onDismiss()
        },
        title = {
            Column {
                Text(
                    text = if (isRequired) "🔗 업비트 연동 (필수)" else "🔗 거래소 연동",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (isRequired) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "USDT/KRW 환율 조회를 위해 업비트 연동이 필요합니다",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // API Key
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it; localError = null },
                    label = { Text("API Key", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color(0xFF2A2D3E),
                        focusedContainerColor = Color(0xFF0F1720),
                        unfocusedContainerColor = Color(0xFF0F1720)
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Secret Key
                OutlinedTextField(
                    value = secretKey,
                    onValueChange = { secretKey = it; localError = null },
                    label = { Text("Secret Key", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color(0xFF2A2D3E),
                        focusedContainerColor = Color(0xFF0F1720),
                        unfocusedContainerColor = Color(0xFF0F1720)
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                // 에러 메시지
                if (localError != null) {
                    Text(
                        text = localError ?: "",
                        color = ErrorColor,
                        fontSize = 12.sp
                    )
                }

                // 안내
                Text(
                    text = "• 출금 권한은 체크하지 마세요\n• API Key는 기기에만 저장됩니다",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (apiKey.isBlank() || secretKey.isBlank()) {
                        localError = "API Key와 Secret Key를 모두 입력하세요"
                        return@TextButton
                    }
                    localError = null
                    // ViewModel에 키 설정 후 비동기 검증+저장 시작
                    viewModel.updateApiKey(ExchangeType.UPBIT, apiKey)
                    viewModel.updateSecretKey(ExchangeType.UPBIT, secretKey)
                    viewModel.saveSelectedCredentials()
                    // onSuccess()는 여기서 호출하지 않음!
                    // LaunchedEffect(uiState.saveSuccess)에서 검증 성공 시에만 호출됨
                },
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = AccentBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("검증 중...", color = TextSecondary)
                } else {
                    Text("연동하기", color = AccentBlue, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isRequired) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !uiState.isLoading
                ) {
                    Text("취소", color = TextSecondary)
                }
            }
        },
        containerColor = Color(0xFF1A1D2E)
    )
}
