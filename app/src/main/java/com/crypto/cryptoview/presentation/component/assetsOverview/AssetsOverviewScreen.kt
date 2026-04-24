package com.crypto.cryptoview.presentation.component.assetsOverview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
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
import com.crypto.cryptoview.domain.model.AggregatedHolding
import com.crypto.cryptoview.domain.model.ExchangeData
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.presentation.component.assetsOverview.chart.ChartData
import com.crypto.cryptoview.presentation.component.assetsOverview.chart.DonutChart
import kotlin.compareTo

@Composable
fun AssetsOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: AssetsOverviewViewModel = hiltViewModel(),
    onNavigateToHoldings: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current

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
            .background(Color(0xFF0F1117))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TotalBalanceCard(
                totalValue = uiState.totalValue,
                totalChange = uiState.totalChange,
                totalChangeRate = uiState.totalChangeRate
            )
        }
        item {
            ExchangeBreakdownCard(exchanges = uiState.exchangeBreakdown)
        }
        item {
            TopHoldingsCard(
                aggregatedHoldings = uiState.topAggregatedHoldings,
                onViewAllClick = onNavigateToHoldings
            )
        }
    }
}

@Composable
private fun TotalBalanceCard(
    totalValue: Double,
    totalChange: Double,
    totalChangeRate: Double
) {
    val isPositive = totalChange >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3949AB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.total_value),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format(Locale.getDefault(), "₩%,.0f", totalValue),
                    color = Color.White,
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
                    text = String.format(Locale.getDefault(), "%s₩%,.0f", if (isPositive) "+" else "", totalChange),
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 18.sp
                )
                Text(
                    text = String.format(Locale.getDefault(), "(%.2f%%)", totalChangeRate),
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ExchangeBreakdownCard(
    exchanges: List<ExchangeData> = emptyList()
) {
    val sortedExchanges = exchanges.sortedByDescending { it.totalValue }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D2E))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Exchange Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
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
                            exchange.exchange.displayName,  // type -> exchangeType
                            exchange.totalValue,
                            exchange.exchange.color  // type -> exchangeType
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
                                name = exchange.exchange.displayName,  // type -> exchangeType
                                color = exchange.exchange.color  // type -> exchangeType
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 거래소 별 금액 표시 (값이 0보다 큰 것만)
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
                                amount = String.format(Locale.getDefault(), "₩%,.0f", exchange.totalValue),
                                color = exchange.exchange.color,  // type -> exchangeType
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = Color.White.copy(alpha = 0.7f),
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
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

/**
 * Top 5 Holdings 카드
 * 심볼 기준 통합된 데이터를 표시하며, 여러 거래소 보유 시 표시
 */
@Composable
private fun TopHoldingsCard(
    aggregatedHoldings: List<AggregatedHolding> = emptyList(),
    onViewAllClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D2E))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top 5 Holdings",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllClick) {
                    Text(text = "View All →", color = Color(0xFF5B7FFF))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                aggregatedHoldings.forEach { holding ->
                    AggregatedHoldingItem(
                        symbol = holding.normalizedSymbol,
                        name = holding.name,
                        totalValue = String.format(Locale.getDefault(), "₩%,.0f", holding.totalValue),
                        change = String.format(Locale.getDefault(), "%s₩%,.0f", if (holding.totalChange >= 0) "+" else "", holding.totalChange),
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

/**
 * 통합된 홀딩 아이템
 * 여러 거래소 보유 시 거래소 수 표시
 */
@Composable
private fun AggregatedHoldingItem(
    symbol: String,
    name: String,
    totalValue: String,
    change: String,
    changePercent: String,
    isPositive: Boolean,
    exchangeCount: Int,
    exchanges: List<com.crypto.cryptoview.domain.model.ExchangeType>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252837))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 심볼 아이콘
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF3949AB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.take(2),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        // 여러 거래소 보유 시 표시
                        if (exchangeCount > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF374151), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${exchangeCount}개 거래소",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    // 거래소 이름 표시 (색상 점으로)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        exchanges.take(3).forEach { exchange ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(exchange.color, CircleShape)
                            )
                        }
                        if (exchanges.size > 3) {
                            Text(
                                text = "+${exchanges.size - 3}",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = totalValue,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$change ($changePercent)",
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252837))
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
                        .background(Color(0xFF5B7FFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.take(3),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = symbol, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = name, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = price, color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = change,
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 12.sp
                )
                Text(
                    text = changePercent,
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 12.sp
                )
            }
        }
    }
}