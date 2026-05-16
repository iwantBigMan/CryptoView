package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.util.Locale
import com.crypto.cryptoview.R
import com.crypto.cryptoview.domain.model.asset.AggregatedHolding
import com.crypto.cryptoview.domain.model.asset.ExchangeData
import com.crypto.cryptoview.domain.model.settings.DisplayCurrency
import com.crypto.cryptoview.presentation.component.assetsOverview.ai.AiPortfolioInsightUiState
import com.crypto.cryptoview.presentation.component.assetsOverview.ai.AiPortfolioInsightViewModel
import com.crypto.cryptoview.presentation.component.assetsOverview.ai.dialog.AiPortfolioInsightDialog
import com.crypto.cryptoview.presentation.component.assetsOverview.chart.ChartData
import com.crypto.cryptoview.presentation.component.assetsOverview.chart.DonutChart
import com.crypto.cryptoview.presentation.main.DisplayCurrencyViewModel
import com.crypto.cryptoview.presentation.model.formatDisplayMoney
import com.crypto.cryptoview.presentation.model.uiColor
import com.crypto.cryptoview.ui.theme.LocalAppColors
import kotlin.compareTo

@Composable
fun AssetsOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: AssetsOverviewViewModel = hiltViewModel(),
    aiPortfolioInsightViewModel: AiPortfolioInsightViewModel = hiltViewModel(),
    displayCurrencyViewModel: DisplayCurrencyViewModel = hiltViewModel(),
    onNavigateToHoldings: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
    val aiInsightUiState = aiPortfolioInsightViewModel.uiState.collectAsState().value
    val displayCurrency by displayCurrencyViewModel.currentCurrency.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val colors = LocalAppColors.current
    var showAiInsightDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.startAutoRefresh()
                Lifecycle.Event.ON_STOP -> viewModel.stopAutoRefresh()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TotalBalanceCard(
                totalValue = uiState.totalValue,
                totalChange = uiState.totalChange,
                totalChangeRate = uiState.totalChangeRate,
                usdtKrwRate = uiState.usdtKrwRate,
                displayCurrency = displayCurrency
            )
        }
        item {
            AiPortfolioInsightLauncherCard(
                uiState = aiInsightUiState,
                onGenerateClick = {
                    showAiInsightDialog = true
                    aiPortfolioInsightViewModel.generateInsight()
                }
            )
        }
        item {
            ExchangeBreakdownCard(
                exchanges = uiState.exchangeBreakdown,
                usdtKrwRate = uiState.usdtKrwRate,
                displayCurrency = displayCurrency
            )
        }
        item {
            TopHoldingsCard(
                aggregatedHoldings = uiState.topAggregatedHoldings,
                usdtKrwRate = uiState.usdtKrwRate,
                displayCurrency = displayCurrency,
                onViewAllClick = onNavigateToHoldings
            )
        }
    }

    if (showAiInsightDialog) {
        AiPortfolioInsightDialog(
            uiState = aiInsightUiState,
            onDismiss = { showAiInsightDialog = false },
            onRetry = aiPortfolioInsightViewModel::generateInsight
        )
    }
}

@Composable
private fun AiPortfolioInsightLauncherCard(
    uiState: AiPortfolioInsightUiState,
    onGenerateClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val isLoading = uiState is AiPortfolioInsightUiState.RefreshingAssets ||
        uiState is AiPortfolioInsightUiState.GeneratingInsight

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI 포트폴리오 요약",
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "현재 자산 상태를 기준으로 요약합니다.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onGenerateClick,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBlue,
                    contentColor = colors.textPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = colors.textPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isLoading) "분석 중" else "분석")
            }
        }
    }
}

@Composable
private fun AiPortfolioInsightCard(
    uiState: AiPortfolioInsightUiState,
    onGenerateClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val isLoading = uiState is AiPortfolioInsightUiState.RefreshingAssets ||
        uiState is AiPortfolioInsightUiState.GeneratingInsight
    val statusText = when (uiState) {
        AiPortfolioInsightUiState.RefreshingAssets -> "전체 자산을 최신화하는 중입니다."
        AiPortfolioInsightUiState.GeneratingInsight -> "AI 포트폴리오 요약을 생성하는 중입니다."
        else -> null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI 포트폴리오 요약",
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "현재 자산 상태를 기준으로 요약합니다.",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onGenerateClick,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accentBlue,
                        contentColor = colors.textPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = colors.textPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isLoading) "분석 중" else "분석")
                }
            }

            statusText?.let { text ->
                Text(
                    text = text,
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )
            }

            when (uiState) {
                is AiPortfolioInsightUiState.Success -> {
                    Text(
                        text = uiState.insight.insight,
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.insight.model,
                            color = colors.textTertiary,
                            fontSize = 11.sp
                        )
                        TextButton(onClick = onClearClick) {
                            Text(text = "닫기", color = colors.accentBlue)
                        }
                    }
                }
                is AiPortfolioInsightUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = colors.negative,
                        fontSize = 13.sp
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(
    totalValue: Double,
    totalChange: Double,
    totalChangeRate: Double,
    usdtKrwRate: Double,
    displayCurrency: DisplayCurrency
) {
    val isPositive = totalChange >= 0
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.accentBlue.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.total_value),
                color = colors.textPrimary.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formatDisplayMoney(totalValue, displayCurrency, usdtKrwRate),
                    color = colors.textPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatDisplayMoney(totalChange, displayCurrency, usdtKrwRate, signed = true),
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 18.sp
                )
                Text(
                    text = String.format(Locale.getDefault(), "(%.2f%%)", totalChangeRate),
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ExchangeBreakdownCard(
    exchanges: List<ExchangeData> = emptyList(),
    usdtKrwRate: Double,
    displayCurrency: DisplayCurrency
) {
    val sortedExchanges = exchanges.sortedByDescending { it.totalValue }
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Exchange Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                DonutChart(
                    data = exchanges.map { exchange ->
                        ChartData(
                            exchange.exchange.displayName,
                            exchange.totalValue,
                            exchange.exchange.uiColor()
                        )
                    }.filter { it.value > 0 },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                exchanges.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { exchange ->
                            ExchangeItem(
                                name = exchange.exchange.displayName,
                                color = exchange.exchange.uiColor()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val visibleExchanges = sortedExchanges.filter { it.totalValue > 0 }

                visibleExchanges.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 0.dp),
                        horizontalArrangement = if (rowItems.size == 1) {
                            Arrangement.Center
                        } else {
                            Arrangement.SpaceBetween
                        }
                    ) {
                        rowItems.forEach { exchange ->
                            ExchangeAmount(
                                name = exchange.exchange.displayName,
                                amount = formatDisplayMoney(exchange.totalValue, displayCurrency, usdtKrwRate),
                                color = exchange.exchange.uiColor(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (rowItems != visibleExchanges.chunked(2).last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExchangeItem(name: String, color: Color) {
    val colors = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = colors.textSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ExchangeAmount(
    name: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary
        )
    }
}

@Composable
private fun TopHoldingsCard(
    aggregatedHoldings: List<AggregatedHolding> = emptyList(),
    usdtKrwRate: Double,
    displayCurrency: DisplayCurrency,
    onViewAllClick: () -> Unit = {}
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top 5 Holdings",
                    color = colors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllClick) {
                    Text(text = "View All →", color = colors.accentBlue)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                aggregatedHoldings.forEach { holding ->
                    AggregatedHoldingItem(
                        symbol = holding.normalizedSymbol,
                        name = holding.name,
                        totalValue = formatDisplayMoney(holding.totalValue, displayCurrency, usdtKrwRate),
                        change = formatDisplayMoney(holding.totalChange, displayCurrency, usdtKrwRate, signed = true),
                        changePercent = String.format(Locale.getDefault(), "%.2f%%", holding.totalChangePercent),
                        isPositive = holding.totalChange >= 0,
                        exchangeCount = holding.holdings.size,
                        exchanges = holding.exchanges
                    )
                }
            }
        }
    }
}

@Composable
private fun AggregatedHoldingItem(
    symbol: String,
    name: String,
    totalValue: String,
    change: String,
    changePercent: String,
    isPositive: Boolean,
    exchangeCount: Int,
    exchanges: List<com.crypto.cryptoview.domain.model.exchange.ExchangeType>
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colors.accentBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.take(2),
                        color = colors.accentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        if (exchangeCount > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(colors.chipUnselected, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${exchangeCount}개 거래소",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        exchanges.take(3).forEach { exchange ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(exchange.uiColor(), CircleShape)
                            )
                        }
                        if (exchanges.size > 3) {
                            Text(
                                text = "+${exchanges.size - 3}",
                                color = colors.textTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = totalValue,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$change ($changePercent)",
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun HoldingItem(
    symbol: String,
    name: String,
    price: String,
    change: String,
    changePercent: String,
    isPositive: Boolean
) {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colors.accentBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.take(3),
                        color = colors.accentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = symbol, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                    Text(text = name, color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = price, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                Text(
                    text = change,
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 12.sp
                )
                Text(
                    text = changePercent,
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 12.sp
                )
            }
        }
    }
}
