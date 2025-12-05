package com.crypto.cryptoview.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypto.cryptoview.presentation.main.MainViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1117))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TotalBalanceCard() }
        item { ExchangeBreakdownCard() }
        item { TopHoldingsCard() }
    }
}

@Composable
private fun TotalBalanceCard() {
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
                    text = "₩1,118,569,820",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(start = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "+ ₩0",
                    color = Color.Green,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ExchangeBreakdownCard() {
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
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Chart Placeholder", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ExchangeItem("Upbit", "₩71,410,000", Color(0xFF2196F3))
                ExchangeItem("Binance", "₩53,064,000", Color(0xFFFFC107))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ExchangeItem("Gate.io", "₩60,467,220", Color(0xFF9C27B0))
                ExchangeItem("Bybit", "₩57,612,720", Color(0xFFE91E63))
            }
        }
    }
}

@Composable
private fun ExchangeItem(name: String, amount: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
        Text(
            text = amount,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TopHoldingsCard() {
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
                TextButton(onClick = { }) {
                    Text(
                        text = "View All →",
                        color = Color(0xFF5B7FFF)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HoldingItem(
                symbol = "BTC",
                name = "김프",
                price = "₩101,406,000",
                change = "+₩5,050,000",
                changePercent = "(5.24%)",
                isPositive = true
            )
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
                        text = symbol.take(2),
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
                        color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontSize = 12.sp
                    )
                    Text(
                        text = changePercent,
                        color = Color.White.copy(alpha = 0.5f),
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
