package com.crypto.cryptoview.presentation.component.holdingColins.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypto.cryptoview.ui.theme.*

@Composable
fun HoldingsScreen(
    holdings: List<CoinHolding> = sampleHoldings,
    selectedSort: SortType = SortType.VALUE
) {
    var currentSort by remember { mutableStateOf(selectedSort) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1117))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Holdings",
                color = TextPrimary,
                fontSize = 24.sp
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search coins...", color = SearchBarText)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = SearchBarText)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF1A1D2E),
                focusedContainerColor = Color(0xFF1A1D2E),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SortType.entries) { sortType ->
                FilterChip(
                    selected = currentSort == sortType,
                    onClick = { currentSort = sortType },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sortType.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                color = TextPrimary
                            )
                            if (currentSort == sortType) {
                                Text(" ↕", color = TextPrimary)
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChipSelectedBg,
                        containerColor = ChipUnselectedBg
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Holdings List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(holdings) { holding ->
                CoinHoldingCard(holding)
            }
        }
    }
}

@Composable
fun CoinHoldingCard(holding: CoinHolding) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D2E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Coin Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(holding.iconColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = holding.symbol.take(2),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = holding.displayName,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        // Kimchi Premium Badge
                        holding.kimchiPremium?.let { premium ->
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .background(KimchiPremiumBg, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "김프 +${premium}%",
                                    color = KimchiPremiumText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Value Column
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₩${String.format("%,d", holding.totalValueKRW)}",
                        color = ValuePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "+₩${String.format("%,d", holding.profitKRW)}",
                        color = ValuePositive,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exchange Holdings
            LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                items(holding.exchangeHoldings) { exchange ->
                    ExchangeHoldingItem(exchange)
                }
            }
        }
    }
}

@Composable
fun ExchangeHoldingItem(exchange: ExchangeHolding) {
    Column {
        Text(
            text = exchange.exchangeName,
            color = TextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = "${exchange.amount} ${exchange.symbol}",
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Text(
            text = "+${exchange.profitPercent}%",
            color = PositiveGreen,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0E27)
@Composable
fun HoldingsScreenPreview() {
    HoldingsScreen()
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1F3A)
@Composable
fun CoinHoldingCardPreview() {
    CoinHoldingCard(sampleHoldings[1]) // ETH with kimchi premium
}