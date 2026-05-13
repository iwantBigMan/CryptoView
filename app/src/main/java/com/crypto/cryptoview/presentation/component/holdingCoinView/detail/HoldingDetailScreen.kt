package com.crypto.cryptoview.presentation.component.holdingCoinView.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.crypto.cryptoview.domain.model.gate.GateIoSpotAveragePrice
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import com.crypto.cryptoview.presentation.main.DisplayCurrencyViewModel
import com.crypto.cryptoview.presentation.model.formatDisplayMoney
import com.crypto.cryptoview.presentation.model.formatDisplayPrice
import com.crypto.cryptoview.ui.theme.LocalAppColors
import java.math.BigDecimal
import java.text.DecimalFormat

@Composable
fun HoldingDetailScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: HoldingDetailViewModel = hiltViewModel(),
    displayCurrencyViewModel: DisplayCurrencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val displayCurrency by displayCurrencyViewModel.currentCurrency.collectAsState()
    val colors = LocalAppColors.current

    LaunchedEffect(symbol) {
        viewModel.setSymbol(symbol)
    }

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .systemBarsPadding()
    ) {
        HoldingDetailHeader(
            symbol = uiState.symbol,
            onBack = onBack
        )

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
                HoldingDetailContent(
                    uiState = uiState,
                    displayCurrency = displayCurrency
                )
            }
        }
    }
}

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
                contentDescription = "뒤로가기",
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

@Composable
private fun HoldingDetailContent(
    uiState: HoldingDetailUiState,
    displayCurrency: DisplayCurrency
) {
    val colors = LocalAppColors.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "거래소별 보유 현황",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(uiState.exchangeHoldings) { holding ->
            ExchangeHoldingCard(
                holding = holding,
                usdtKrwRate = uiState.usdtKrwRate,
                displayCurrency = displayCurrency,
                gateIoAveragePriceState = if (holding.exchange == ExchangeType.GATEIO) {
                    uiState.gateIoAveragePriceState
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun ExchangeHoldingCard(
    holding: ExchangeHoldingDetail,
    usdtKrwRate: Double,
    displayCurrency: DisplayCurrency,
    gateIoAveragePriceState: GateIoAveragePriceUiState? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                CurrencyTag(displayCurrency = displayCurrency)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn(
                    label = "수량",
                    value = formatQuantity(holding.quantity),
                    valueColor = colors.accentBlue,
                    modifier = Modifier.weight(1f)
                )
                InfoColumn(
                    label = "평균 단가",
                    value = averagePriceText(holding, displayCurrency, usdtKrwRate),
                    valueColor = colors.accentBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            GateIoAveragePriceInfo(
                state = gateIoAveragePriceState,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn(
                    label = "현재가",
                    value = formatDisplayPrice(holding.currentPrice, holding.currencyUnit, displayCurrency, usdtKrwRate),
                    valueColor = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                InfoColumn(
                    label = "평가 금액",
                    value = formatDisplayMoney(holding.valueKrw, displayCurrency, usdtKrwRate),
                    valueColor = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = colors.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            ProfitLossRow(
                holding = holding,
                usdtKrwRate = usdtKrwRate,
                displayCurrency = displayCurrency
            )
        }
    }
}

@Composable
private fun GateIoAveragePriceInfo(
    state: GateIoAveragePriceUiState?,
    modifier: Modifier = Modifier
) {
    if (state == null) return

    val colors = LocalAppColors.current
    when {
        state.isLoading -> {
            Text(
                text = "Gate.io 평균단가 조회 중...",
                color = colors.textTertiary,
                fontSize = 12.sp,
                modifier = modifier
            )
        }

        state.errorMessage != null -> {
            Text(
                text = state.errorMessage,
                color = if (state.errorType == GateIoAveragePriceErrorType.CREDENTIAL_NOT_FOUND) {
                    colors.accentBlue
                } else {
                    colors.error
                },
                fontSize = 12.sp,
                modifier = modifier
            )
        }

        state.data != null -> {
            GateIoAveragePriceSummary(
                averagePrice = state.data,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun GateIoAveragePriceSummary(
    averagePrice: GateIoSpotAveragePrice,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val hasAveragePrice = (averagePrice.averagePriceValue?.signum() ?: 0) > 0
    val hasTotalCost = (averagePrice.totalCostValue?.signum() ?: 0) > 0

    Column(modifier = modifier.fillMaxWidth()) {
        if (!hasAveragePrice || !hasTotalCost) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "평균단가 계산에 필요한 매수 거래 내역이 없습니다.",
                color = colors.textTertiary,
                fontSize = 12.sp
            )
        }

        if (averagePrice.quantity != averagePrice.currentQuantity) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "계산 수량과 현재 보유 수량이 다릅니다.",
                color = colors.textTertiary,
                fontSize = 12.sp
            )
        }

    }
}

private fun averagePriceText(
    holding: ExchangeHoldingDetail,
    displayCurrency: DisplayCurrency,
    usdtKrwRate: Double
): String {
    return holding.avgBuyPrice?.let {
        formatDisplayPrice(it, holding.currencyUnit, displayCurrency, usdtKrwRate)
    } ?: "-"
}

@Composable
private fun CurrencyTag(displayCurrency: DisplayCurrency) {
    val (backgroundColor, textColor) = when (displayCurrency) {
        DisplayCurrency.KRW -> Color(0xFF1A3A4A) to Color(0xFFBFD6E9)
        DisplayCurrency.USDT -> Color(0xFF3A4A1A) to Color(0xFFD6E9BF)
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayCurrency.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

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


@Composable
private fun ProfitLossRow(
    holding: ExchangeHoldingDetail,
    usdtKrwRate: Double,
    displayCurrency: DisplayCurrency
) {
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
            Text(
                text = "${formatDisplayMoney(holding.profitLoss, displayCurrency, usdtKrwRate, signed = true)} (${String.format("%.2f", holding.profitLossPercent)}%)",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(text = "평단 정보 없음", color = colors.textTertiary, fontSize = 12.sp)
        }
    }
}

private fun formatDecimal(value: BigDecimal, maxFractionDigits: Int): String {
    val pattern = buildString {
        append("#,##0")
        if (maxFractionDigits > 0) {
            append(".")
            repeat(maxFractionDigits) { append("#") }
        }
    }
    return DecimalFormat(pattern).format(value)
}

private fun formatQuantity(quantity: Double): String {
    return if (quantity % 1.0 == 0.0) {
        String.format("%.0f", quantity)
    } else {
        String.format("%.6f", quantity).trimEnd('0').trimEnd('.')
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
private fun PreviewExchangeHoldingCard() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExchangeHoldingCard(
            usdtKrwRate = 1300.0,
            displayCurrency = DisplayCurrency.KRW,
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

        ExchangeHoldingCard(
            usdtKrwRate = 1300.0,
            displayCurrency = DisplayCurrency.KRW,
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
        displayCurrency = DisplayCurrency.KRW,
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
