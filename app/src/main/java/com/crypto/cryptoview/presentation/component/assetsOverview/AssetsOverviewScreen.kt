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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.crypto.cryptoview.presentation.component.assetsOverview.chart.ChartData
import com.crypto.cryptoview.presentation.component.assetsOverview.chart.DonutChart

@Composable
fun AssetsOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: AssetsOverviewViewModel,
    onNavigateToHoldings: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.startAutoRefresh()
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.stopAutoRefresh()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                holdings = uiState.topHoldings,
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
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Value",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "₩${String.format("%,.0f", totalValue)}",
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
                    text = "${if (isPositive) "+" else ""}₩${String.format("%,.0f", totalChange)}",
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 18.sp
                )
                Text(
                    text = "(${String.format("%.2f", totalChangeRate)}%)",
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
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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
                           exchange.type.displayName,
                           exchange.totalValue,
                           exchange.type.color
                       )
                   }.filter { it.value > 0 },
                   modifier = Modifier.fillMaxWidth()
               )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                exchanges.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { exchange ->
                            ExchangeItem(
                                name = exchange.type.displayName,
                                color = exchange.type.color
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    /* 거래소 별 금액 표시
                    * 양쪽으로 정렬 2개 이후 줄바꿈
                    * */
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
                                    name = exchange.type.displayName,
                                    amount = "${String.format("%,.0f", exchange.totalValue)}",
                                    color = exchange.type.color,
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
}

@Composable
private fun ExchangeItem(name: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
@Composable
private fun TopHoldingsCard(
    holdings: List<HoldingData> = emptyList(),
    onViewAllClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D2E))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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
                    Text(
                        text = "View All →",
                        color = Color(0xFF5B7FFF)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                holdings.sortedByDescending { it.totalValue }.take(5).forEach { holding ->
                    HoldingItem(
                        symbol = holding.symbol,
                        name = holding.name,
                        price = "₩${String.format("%,.0f", holding.totalValue)}",
                        change = "${if (holding.change >= 0) "+" else ""}₩${String.format("%,.0f", holding.change)}",
                        changePercent = "${String.format("%.2f", holding.changePercent)}%",
                        isPositive = holding.change >= 0
                    )
                }
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Text(
                        text = symbol,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = name,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = price,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
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
