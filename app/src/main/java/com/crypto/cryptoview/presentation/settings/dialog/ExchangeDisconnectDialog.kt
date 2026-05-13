package com.crypto.cryptoview.presentation.settings.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.ui.theme.LocalAppColors

@Composable
fun ExchangeDisconnectDialog(
    exchange: ExchangeType,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "${exchange.displayName} 연동 해제",
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "${exchange.displayName} credential을 백엔드에서 삭제하고 이 기기의 연동 상태를 해제합니다.\n계속하시겠습니까?",
                color = colors.textSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
            ) {
                Text("연동 해제")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(contentColor = colors.textSecondary)
            ) {
                Text("취소")
            }
        },
        containerColor = colors.cardBackground
    )
}
