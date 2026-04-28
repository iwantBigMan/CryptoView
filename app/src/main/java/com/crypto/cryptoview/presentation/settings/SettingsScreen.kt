package com.crypto.cryptoview.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
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
import com.crypto.cryptoview.domain.model.AppTheme
import com.crypto.cryptoview.domain.model.ExchangeType
import com.crypto.cryptoview.presentation.login.GoogleLoginViewModel
import com.crypto.cryptoview.presentation.main.ThemeViewModel
import com.crypto.cryptoview.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: ExchangeSettingsViewModel = hiltViewModel(),
    googleLoginViewModel: GoogleLoginViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    showExchangeSetup: Boolean = false,
    onLogout: () -> Unit = {},
    onExchangeLinked: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val googleUiState by googleLoginViewModel.uiState.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExchangeDialog by remember { mutableStateOf(showExchangeSetup) }

    val colors = LocalAppColors.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.backgroundPrimary
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
                    color = colors.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Google 계정 정보
            item {
                Text(
                    text = "계정",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
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
                            tint = colors.accentBlue,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = googleUiState.userName ?: "사용자",
                                color = colors.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = googleUiState.userEmail ?: "",
                                color = colors.textSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
            // 연동된 거래소 섹션
            item {
                Text(
                    text = "연동된 거래소",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 연동된 거래소 목록
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.savedCredentials.isEmpty()) {
                            Text(
                                text = "연동된 거래소가 없습니다",
                                color = colors.textSecondary,
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
                                            tint = colors.accentBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = exchange.displayName,
                                            color = colors.textPrimary,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Text(
                                        text = "연동됨",
                                        color = colors.positive,
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
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
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
                            tint = colors.accentBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "거래소 연동 추가",
                            color = colors.accentBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            // 테마 설정
            item {
                Text(
                    text = "테마",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        listOf(
                            AppTheme.DARK   to "다크 모드",
                            AppTheme.LIGHT  to "라이트 모드"
                        ).forEach { (theme, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { themeViewModel.setTheme(theme) }
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, color = colors.textPrimary, fontSize = 15.sp)
                                RadioButton(
                                    selected = currentTheme == theme,
                                    onClick = { themeViewModel.setTheme(theme) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colors.accentBlue,
                                        unselectedColor = colors.textSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }



            // 로그아웃 버튼
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
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
                            tint = colors.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "로그아웃",
                            color = colors.error,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // 앱 정보
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "CryptoView", color = colors.textSecondary, fontSize = 12.sp)
                    Text(text = "Version 1.0.0", color = colors.textTertiary, fontSize = 10.sp)
                }
            }
        }
    }

    // 로그아웃 확인 다이얼로그
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "로그아웃", color = colors.textPrimary) },
            text = {
                Text(
                    text = "Google 계정에서 로그아웃하고\n모든 연동된 거래소 정보가 삭제됩니다.\n계속하시겠습니까?",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                // 모든 로그아웃 (백엔드 + Google + 로컬) 한 곳에서 관리
                                viewModel.logout()

                                showLogoutDialog = false
                                onLogout()
                            } catch (e: Exception) {
                                android.util.Log.e("SettingsScreen", "로그아웃 중 오류", e)
                                showLogoutDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
                ) { Text("로그아웃") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.textSecondary)
                ) { Text("취소") }
            },
            containerColor = colors.cardBackground
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

    val colors = LocalAppColors.current  // 테마 색상

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
                        color = colors.textSecondary,
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
                    label = { Text("API Key", color = colors.textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = colors.accentBlue,
                        unfocusedBorderColor = Color(0xFF2A2D3E),
                        focusedContainerColor = Color(0xFF0F1720),
                        unfocusedContainerColor = Color(0xFF0F1720)
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Secret Key
                // secretKey는 민감 정보이므로 입력 시 선택/복사 방지
                DisableSelection {
                    OutlinedTextField(
                        value = secretKey,
                        onValueChange = { secretKey = it; localError = null },
                        label = { Text("Secret Key", color = colors.textSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colors.accentBlue,
                            unfocusedBorderColor = Color(0xFF2A2D3E),
                            focusedContainerColor = Color(0xFF0F1720),
                            unfocusedContainerColor = Color(0xFF0F1720)
                        ),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                // 에러 메시지
                if (localError != null) {
                    Text(
                        text = localError ?: "",
                        color = colors.error,
                        fontSize = 12.sp
                    )
                }
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
                    // LaunchedEffect(uiState.saveSuccess)에서 검증 성공 시에만 호출됨
                },
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = colors.accentBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("검증 중...", color = colors.textSecondary)
                } else {
                    Text("연동하기", color = colors.accentBlue, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isRequired) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !uiState.isLoading
                ) {
                    Text("취소", color = colors.textSecondary)
                }
            }
        },
        containerColor = Color(0xFF1A1D2E)
    )
}
