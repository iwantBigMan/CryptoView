package com.crypto.cryptoview.presentation.component.holdingCoinView

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.domain.model.asset.CurrencyUnit
import com.crypto.cryptoview.domain.model.asset.ExchangeHoldingDetail
import com.crypto.cryptoview.domain.model.exchange.ExchangeType
import com.crypto.cryptoview.ui.theme.LocalAppColors

/**
 * 보유 상세 화면
 * 거래소별 보유 정보를 표시
 */
@Composable
fun HoldingDetailScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: HoldingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    // 심볼이 변경되면 ViewModel에 설정
    androidx.compose.runtime.LaunchedEffect(symbol) {
        viewModel.setSymbol(symbol)
    }

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .systemBarsPadding()
    ) {
        // 상단 헤더
        HoldingDetailHeader(
            symbol = uiState.symbol,
            onBack = onBack
        )

        // 콘텐츠
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.accentBlue)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "오류가 발생했습니다",
                        color = colors.error
                    )
                }
            }
            uiState.exchangeHoldings.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "보유 정보가 없습니다",
                        color = colors.textSecondary
                    )
                }
            }
            else -> {
                HoldingDetailContent(uiState = uiState)
            }
        }
    }
}

/**
 * 헤더 컴포넌트
 */
@Composable
private fun HoldingDetailHeader(
    symbol: String,
    onBack: () -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = symbol,
            color = colors.textPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 상세 콘텐츠
 */
@Composable
private fun HoldingDetailContent(uiState: HoldingDetailUiState) {
    val colors = LocalAppColors.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 섹션 타이틀
        item {
            Text(
                text = "거래소별 보유 현황",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // 거래소별 카드
        items(uiState.exchangeHoldings) { holding ->
            ExchangeHoldingCard(holding = holding)
        }
    }
}

/**
 * 거래소별 보유 카드
 */
@Composable
fun ExchangeHoldingCard(
    holding: ExchangeHoldingDetail,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 거래소 이름 + 화폐 태그
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = holding.exchange.displayName,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                CurrencyTag(currencyUnit = holding.currencyUnit)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 수량 & 평단가
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn(
                    label = "수량",
                    value = formatQuantity(holding.quantity),
                    valueColor = colors.accentBlue,
                    modifier = Modifier.weight(1f)
                )
                InfoColumn(
                    label = "평균 단가",
                    value = holding.avgBuyPrice?.let {
                        formatPrice(it, holding.currencyUnit)
                    } ?: "-",
                    valueColor = colors.accentBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 현재가 & 평가금액
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn(
                    label = "현재가",
                    value = formatPrice(holding.currentPrice, holding.currencyUnit),
                    valueColor = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                InfoColumn(
                    label = "평가 금액 (KRW)",
                    value = "₩${formatNumber(holding.valueKrw)}",
                    valueColor = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = colors.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // 손익
            ProfitLossRow(holding = holding)
        }
    }
}

/**
 * 화폐 단위 태그
 */
@Composable
private fun CurrencyTag(currencyUnit: CurrencyUnit) {
    // 화폐 태그는 고정 색상 사용 (의미론적 색상)
    val (backgroundColor, textColor) = when (currencyUnit) {
        CurrencyUnit.KRW  -> Color(0xFF1A3A4A) to Color(0xFFBFD6E9)
        CurrencyUnit.USDT -> Color(0xFF3A4A1A) to Color(0xFFD6E9BF)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = currencyUnit.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 정보 컬럼
 */
@Composable
private fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified
) {
    val colors = LocalAppColors.current
    val effectiveValueColor = if (valueColor == Color.Unspecified) colors.accentBlue else valueColor
    Column(modifier = modifier) {
        Text(text = label, color = colors.textSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = effectiveValueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * 손익 표시 Row
 */
@Composable
private fun ProfitLossRow(holding: ExchangeHoldingDetail) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "손익", color = colors.textSecondary, fontSize = 12.sp)

        if (holding.profitLoss != null && holding.profitLossPercent != null) {
            val isPositive = holding.profitLoss >= 0
            val color = if (isPositive) colors.positive else colors.negative
            val sign = if (isPositive) "+" else ""

            Text(
                text = "${sign}₩${formatNumber(holding.profitLoss)} (${String.format("%.2f", holding.profitLossPercent)}%)",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(text = "평단 정보 없음", color = colors.textTertiary, fontSize = 12.sp)
        }
    }
}

// 포맷 유틸 함수들
private fun formatPrice(value: Double, unit: CurrencyUnit): String {
    return when (unit) {
        CurrencyUnit.KRW  -> "₩${formatNumber(value)}"
        CurrencyUnit.USDT -> "$${formatNumber(value)}"
    }
}

private fun formatNumber(value: Double): String = String.format("%,.0f", value)

private fun formatQuantity(quantity: Double): String {
    return if (quantity % 1.0 == 0.0) {
        String.format("%.0f", quantity)
    } else {
        String.format("%.6f", quantity).trimEnd('0').trimEnd('.')
    }
}

// Preview
@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
private fun PreviewExchangeHoldingCard() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 업비트 (평단 있음)
        ExchangeHoldingCard(
            holding = ExchangeHoldingDetail(
                exchange = ExchangeType.UPBIT,
                symbol = "BTC",
                quantity = 0.5,
                avgBuyPrice = 85000000.0,
                currentPrice = 88500000.0,
                currencyUnit = CurrencyUnit.KRW,
                valueKrw = 44250000.0,
                profitLoss = 1750000.0,
                profitLossPercent = 4.12
            )
        )

        // Gate.io (평단 없음 - USDT)
        ExchangeHoldingCard(
            holding = ExchangeHoldingDetail(
                exchange = ExchangeType.GATEIO,
                symbol = "BTC",
                quantity = 0.3,
                avgBuyPrice = null,
                currentPrice = 66500.0,
                currencyUnit = CurrencyUnit.USDT,
                valueKrw = 26334000.0,
                profitLoss = null,
                profitLossPercent = null
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
private fun PreviewHoldingDetailContent() {
    HoldingDetailContent(
        uiState = HoldingDetailUiState(
            symbol = "BTC",
            coinName = "Bitcoin",
            totalValueKrw = 70584000.0,
            totalProfitLoss = 1750000.0,
            totalProfitLossPercent = 2.54,
            exchangeHoldings = listOf(
                ExchangeHoldingDetail(
                    exchange = ExchangeType.UPBIT,
                    symbol = "BTC",
                    quantity = 0.5,
                    avgBuyPrice = 85000000.0,
                    currentPrice = 88500000.0,
                    currencyUnit = CurrencyUnit.KRW,
                    valueKrw = 44250000.0,
                    profitLoss = 1750000.0,
                    profitLossPercent = 4.12
                ),
                ExchangeHoldingDetail(
                    exchange = ExchangeType.GATEIO,
                    symbol = "BTC",
                    quantity = 0.3,
                    avgBuyPrice = null,
                    currentPrice = 66500.0,
                    currencyUnit = CurrencyUnit.USDT,
                    valueKrw = 26334000.0,
                    profitLoss = null,
                    profitLossPercent = null
                )
            )
        )
    )
}
