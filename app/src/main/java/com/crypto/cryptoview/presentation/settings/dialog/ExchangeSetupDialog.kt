package com.crypto.cryptoview.presentation.settings.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.presentation.settings.ExchangeSettingsUiState
import com.crypto.cryptoview.ui.theme.LocalAppColors

@Composable
fun ExchangeSetupDialog(
    isRequired: Boolean,
    uiState: ExchangeSettingsUiState,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onErrorConsumed: () -> Unit,
    onSaveCredential: (ExchangeType, String, String) -> Unit
) {
    val colors = LocalAppColors.current
    var selectedExchange by remember { mutableStateOf(ExchangeType.UPBIT) }
    var apiKey by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) onSuccess()
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            localError = uiState.error
            onErrorConsumed()
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isRequired && !uiState.isLoading) onDismiss()
        },
        title = {
            Column {
                Text(
                    text = if (isRequired) "Upbit 연동 (필수)" else "거래소 연동",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (isRequired) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "USDT/KRW 환율 조회를 위해 Upbit 연동이 필요합니다.",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!isRequired) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(ExchangeType.UPBIT, ExchangeType.GATEIO).forEach { exchange ->
                            FilterChip(
                                selected = selectedExchange == exchange,
                                onClick = {
                                    selectedExchange = exchange
                                    apiKey = ""
                                    secretKey = ""
                                    localError = null
                                },
                                label = { Text(exchange.displayName) },
                                enabled = !uiState.isLoading
                            )
                        }
                    }
                } else {
                    selectedExchange = ExchangeType.UPBIT
                }

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = {
                        apiKey = it
                        localError = null
                    },
                    label = { Text("API Key", color = colors.textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accentBlue,
                        unfocusedBorderColor = colors.surfaceVariant,
                        focusedContainerColor = colors.cardBackground,
                        unfocusedContainerColor = colors.cardBackground
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                DisableSelection {
                    OutlinedTextField(
                        value = secretKey,
                        onValueChange = {
                            secretKey = it
                            localError = null
                        },
                        label = { Text("Secret Key", color = colors.textSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedBorderColor = colors.accentBlue,
                            unfocusedBorderColor = colors.surfaceVariant,
                            focusedContainerColor = colors.cardBackground,
                            unfocusedContainerColor = colors.cardBackground
                        ),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                localError?.let { message ->
                    Text(
                        text = message,
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
                        localError = "API Key와 Secret Key를 모두 입력하세요."
                        return@TextButton
                    }
                    localError = null
                    onSaveCredential(selectedExchange, apiKey, secretKey)
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
        containerColor = colors.cardBackground
    )
}
