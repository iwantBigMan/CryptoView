package com.crypto.cryptoview.presentation.component.holdingColins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.presentation.component.holdingColins.preview.SortType

/**
 * 보유 코인 화면
 * - 전체 거래소 보유 코인 통합 표시
 * - 검색/정렬 기능
 */
@Composable
fun HoldingsScreen(
    modifier: Modifier = Modifier,
    viewModel: HoldingCoinsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1117))
            .padding(16.dp)
    ) {
        // 헤더
        Text(
            text = "My Holdings",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 검색바
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 정렬 필터
        SortFilterRow(
            selectedSort = uiState.sortType,
            onSortChange = { viewModel.onSortTypeChange(it) },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 보유 코인 목록
        if (uiState.filteredHoldings.isEmpty()) {
            EmptyHoldingsView()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredHoldings) { holding ->
                    HoldingCard(holding = holding)
                }
            }
        }
    }
}

/**
 * 검색바
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search coins...", color = Color.Gray) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1A1D2E),
            unfocusedContainerColor = Color(0xFF1A1D2E),
            focusedBorderColor = Color(0xFF5B7FFF),
            unfocusedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * 정렬 필터 Row
 */
@Composable
private fun SortFilterRow(
    selectedSort: SortType,
    onSortChange: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SortType.entries) { sortType ->
            FilterChip(
                selected = selectedSort == sortType,
                onClick = { onSortChange(sortType) },
                label = {
                    Text(
                        text = when (sortType) {
                            SortType.VALUE -> "Value"
                            SortType.PROFIT -> "Profit"
                            SortType.SYMBOL -> "Symbol"
                        },
                        fontSize = 14.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF5B7FFF),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF252837),
                    labelColor = Color.White.copy(alpha = 0.7f)
                )
            )
        }
    }
}

/**
 * 보유 코인 카드
 */
@Composable
private fun HoldingCard(holding: HoldingData) {
    val isPositive = holding.change >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D2E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 코인 정보 (왼쪽)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 코인 아이콘
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF5B7FFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = holding.symbol.take(3),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = holding.symbol,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = holding.exchange.displayName,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${String.format("%.6f", holding.balance)} ${holding.symbol}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }

            // 가격 정보 (오른쪽)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₩${String.format("%,.0f", holding.totalValue)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${if (isPositive) "+" else ""}₩${String.format("%,.0f", holding.change)}",
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 14.sp
                )
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format("%.2f", holding.changePercent)}%",
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 빈 상태 뷰
 */
@Composable
private fun EmptyHoldingsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No holdings found",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Connect your exchange to see holdings",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        }
    }
}