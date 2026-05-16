package com.crypto.cryptoview.presentation.component.assetsOverview.ai.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.crypto.cryptoview.presentation.component.assetsOverview.ai.AiPortfolioInsightUiState
import com.crypto.cryptoview.ui.theme.LocalAppColors

@Composable
fun AiPortfolioInsightDialog(
    uiState: AiPortfolioInsightUiState,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    val colors = LocalAppColors.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 580.dp),
            shape = RoundedCornerShape(18.dp),
            color = colors.cardBackground,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                DialogHeader(onDismiss = onDismiss)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(min = 220.dp)
                        .background(colors.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    when (uiState) {
                        AiPortfolioInsightUiState.Idle,
                        AiPortfolioInsightUiState.RefreshingAssets,
                        AiPortfolioInsightUiState.GeneratingInsight -> LoadingContent(uiState)

                        is AiPortfolioInsightUiState.Success -> {
                            InsightTextContent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                paragraphs = uiState.insightParagraphs
                            )
                        }

                        is AiPortfolioInsightUiState.Error -> {
                            ErrorContent(
                                message = uiState.message,
                                onRetry = onRetry
                            )
                        }
                    }
                }

                if (uiState is AiPortfolioInsightUiState.Success) {
                    Text(
                        text = "Model ${uiState.insight.model}",
                        color = colors.textTertiary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightTextContent(
    paragraphs: List<String>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        paragraphs.forEach { paragraph ->
            Text(
                text = paragraph,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                fontSize = 14.sp,
                lineHeight = 23.sp
            )
        }
    }
}

@Composable
private fun DialogHeader(onDismiss: () -> Unit) {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(colors.accentBlue.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = colors.accentBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "AI 포트폴리오 요약",
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "현재 자산 상태를 기준으로 생성된 요약입니다.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        FilledTonalButton(
            onClick = onDismiss,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = colors.surfaceVariant,
                contentColor = colors.textSecondary
            )
        ) {
            Text(text = "닫기")
        }
    }
}

@Composable
private fun LoadingContent(uiState: AiPortfolioInsightUiState) {
    val colors = LocalAppColors.current
    val message = when (uiState) {
        AiPortfolioInsightUiState.RefreshingAssets -> "전체 자산을 최신화하는 중입니다."
        AiPortfolioInsightUiState.GeneratingInsight -> "AI 요약을 생성하는 중입니다."
        else -> "AI 요약을 준비하는 중입니다."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = colors.accentBlue,
            strokeWidth = 3.dp,
            modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.size(14.dp))
        Text(
            text = message,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "분석 결과가 도착하면 바로 표시됩니다.",
            color = colors.textSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = message,
            color = colors.negative,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = onRetry,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.accentBlue,
                    containerColor = Color.Transparent
                )
            ) {
                Text(text = "다시 분석")
            }
        }
    }
}
