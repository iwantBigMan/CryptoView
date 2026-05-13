package com.crypto.cryptoview.presentation.settings.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.crypto.cryptoview.ui.theme.LocalAppColors

@Composable
fun LogoutConfirmDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = "로그아웃", color = colors.textPrimary) },
        text = {
            Text(
                text = "Google 계정에서 로그아웃하고 모든 거래소 연동 정보를 정리합니다.\n계속하시겠습니까?",
                color = colors.textSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
            ) {
                Text("로그아웃")
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
