package com.crypto.cryptoview.presentation.component.holdingCoinView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.crypto.cryptoview.domain.model.AggregatedHolding
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.presentation.component.holdingCoinView.preview.SortType
import com.crypto.cryptoview.ui.theme.LocalAppColors

/**
 * 보유 코인 화면
 * - 전체 거래소 보유 코인 통합 표시
 * - 검색/정렬 기능
 */
@Composable
fun HoldingsScreen(
    modifier: Modifier = Modifier,
    viewModel: HoldingCoinsViewModel = hiltViewModel(),
    onHoldingClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val colors = LocalAppColors.current

    LaunchedEffect(viewModel) {
        viewModel.startAutoRefresh()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.startAutoRefresh()
                Lifecycle.Event.ON_STOP -> viewModel.stopAutoRefresh()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopAutoRefresh()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .padding(16.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Holdings",
                color = colors.textPrimary,
                fontSize = 24.sp
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        // 보유 코인 목록 (심볼 기준 통합)
        if (uiState.filteredAggregatedHoldings.isEmpty()) {
            EmptyHoldingsView()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredAggregatedHoldings) { holding ->
                    AggregatedHoldingCard(
                        holding = holding,
                        onClick = { onHoldingClick(holding.normalizedSymbol) }
                    )
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
    val colors = LocalAppColors.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search coins...", color = colors.textTertiary) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = colors.textTertiary)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            focusedBorderColor = colors.accentBlue,
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
    val colors = LocalAppColors.current
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
                            SortType.VALUE  -> "Value"
                            SortType.PROFIT -> "Profit"
                            SortType.SYMBOL -> "Symbol"
                        },
                        fontSize = 14.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.chipSelected,
                    selectedLabelColor = colors.textPrimary,
                    containerColor = colors.chipUnselected,
                    labelColor = colors.textSecondary
                )
            )
        }
    }
}

/**
 * 통합 보유 코인 카드 (심볼 기준 통합)
 * 여러 거래소 보유 시 거래소 정보 표시
 */
@Composable
private fun AggregatedHoldingCard(
    holding: AggregatedHolding,
    onClick: () -> Unit = {}
) {
    val isPositive = holding.totalChange >= 0
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.accentBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = holding.normalizedSymbol.take(3),
                        color = colors.accentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = holding.normalizedSymbol,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        // 여러 거래소 보유 시 표시
                        if (holding.holdings.size > 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(colors.chipUnselected, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${holding.holdings.size}개 거래소",
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    // 총 보유량
                    Text(
                        text = String.format(Locale.getDefault(), "%.4f %s", holding.totalBalance, holding.normalizedSymbol),
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                    // 거래소 색상 점
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        holding.exchanges.take(4).forEach { exchange ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(exchange.color, CircleShape)
                            )
                        }
                        if (holding.exchanges.size > 4) {
                            Text(
                                text = "+${holding.exchanges.size - 4}",
                                color = colors.textTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 가격 정보 (오른쪽)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "₩%,.0f", holding.totalValue),
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%s₩%,.0f (%,.2f%%)",
                        if (isPositive) "+" else "",
                        holding.totalChange,
                        holding.totalChangePercent
                    ),
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * 보유 코인 카드 (거래소별 개별 - 디테일용)
 */
@Composable
private fun HoldingCard(holding: HoldingData, onClick: () -> Unit = {}) {
    val isPositive = holding.change >= 0
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.accentBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = holding.symbol.take(3),
                        color = colors.accentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = holding.symbol,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f %s", holding.balance, holding.symbol),
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // 가격 정보 (오른쪽)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "₩%,.0f", holding.totalValue),
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = String.format(Locale.getDefault(), "%s₩%,.0f (%,.2f%%)", if (isPositive) "+" else "", holding.change, holding.changePercent),
                    color = if (isPositive) colors.positive else colors.negative,
                    fontSize = 14.sp
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
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No holdings found",
                color = colors.textSecondary,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Connect your exchange to see holdings",
                color = colors.textTertiary,
                fontSize = 14.sp
            )
        }
    }
}
