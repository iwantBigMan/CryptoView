package com.crypto.cryptoview.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.domain.model.settings.AppTheme
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import com.crypto.cryptoview.presentation.login.GoogleLoginViewModel
import com.crypto.cryptoview.presentation.main.DisplayCurrencyViewModel
import com.crypto.cryptoview.presentation.main.ThemeViewModel
import com.crypto.cryptoview.presentation.settings.dialog.ExchangeDisconnectDialog
import com.crypto.cryptoview.presentation.settings.dialog.ExchangeSetupDialog
import com.crypto.cryptoview.presentation.settings.dialog.LogoutConfirmDialog
import com.crypto.cryptoview.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: ExchangeSettingsViewModel = hiltViewModel(),
    googleLoginViewModel: GoogleLoginViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    displayCurrencyViewModel: DisplayCurrencyViewModel = hiltViewModel(),
    showExchangeSetup: Boolean = false,
    onLogout: () -> Unit = {},
    onExchangeLinked: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val googleUiState by googleLoginViewModel.uiState.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val currentCurrency by displayCurrencyViewModel.currentCurrency.collectAsState()
    val scope = rememberCoroutineScope()
    val colors = LocalAppColors.current

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExchangeDialog by remember { mutableStateOf(showExchangeSetup) }
    var exchangeToDelete by remember { mutableStateOf<ExchangeType?>(null) }

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
            item {
                Text(
                    text = "설정",
                    color = colors.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            item {
                SectionTitle("계정")
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

            item {
                SectionTitle("연동된 거래소")
                LinkedExchangeCard(
                    savedCredentials = uiState.savedCredentials,
                    isLoading = uiState.isLoading,
                    onDeleteClick = { exchangeToDelete = it }
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isLoading) { showExchangeDialog = true },
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

            item {
                SectionTitle("테마")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        listOf(
                            AppTheme.DARK to "다크 모드",
                            AppTheme.LIGHT to "라이트 모드"
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

            item {
                SectionTitle("표시 통화")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            DisplayCurrency.KRW to "KRW",
                            DisplayCurrency.USDT to "USDT"
                        ).forEach { (currency, label) ->
                            FilterChip(
                                selected = currentCurrency == currency,
                                onClick = { displayCurrencyViewModel.setCurrency(currency) },
                                label = {
                                    Text(
                                        text = label,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 37.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = currentCurrency == currency
                                ),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colors.chipSelected,
                                    selectedLabelColor = colors.textPrimary,
                                    containerColor = colors.chipUnselected,
                                    labelColor = colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isLoading) { showLogoutDialog = true },
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
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

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            isLoading = uiState.isLoading,
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                scope.launch {
                    try {
                        viewModel.logout()
                        showLogoutDialog = false
                        onLogout()
                    } catch (e: Exception) {
                        android.util.Log.e("SettingsScreen", "logout failed", e)
                        showLogoutDialog = false
                    }
                }
            }
        )
    }

    exchangeToDelete?.let { exchange ->
        ExchangeDisconnectDialog(
            exchange = exchange,
            isLoading = uiState.isLoading,
            onDismiss = { exchangeToDelete = null },
            onConfirm = {
                viewModel.deleteCredentials(exchange)
                exchangeToDelete = null
            }
        )
    }

    if (showExchangeDialog) {
        ExchangeSetupDialog(
            isRequired = showExchangeSetup && uiState.savedCredentials.isEmpty(),
            uiState = uiState,
            onDismiss = {
                if (!showExchangeSetup || uiState.savedCredentials.isNotEmpty()) {
                    showExchangeDialog = false
                }
            },
            onSuccess = {
                viewModel.clearSaveSuccess()
                showExchangeDialog = false
                onExchangeLinked()
            },
            onErrorConsumed = viewModel::clearError,
            onSaveCredential = viewModel::saveCredential
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    val colors = LocalAppColors.current
    Text(
        text = text,
        color = colors.textSecondary,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun LinkedExchangeCard(
    savedCredentials: List<ExchangeType>,
    isLoading: Boolean,
    onDeleteClick: (ExchangeType) -> Unit
) {
    val colors = LocalAppColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (savedCredentials.isEmpty()) {
                Text(
                    text = "연동된 거래소가 없습니다",
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
            } else {
                savedCredentials.forEach { exchange ->
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "연동됨",
                                color = colors.positive,
                                fontSize = 12.sp
                            )
                            TextButton(
                                onClick = { onDeleteClick(exchange) },
                                enabled = !isLoading,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = colors.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "해제",
                                    color = colors.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
