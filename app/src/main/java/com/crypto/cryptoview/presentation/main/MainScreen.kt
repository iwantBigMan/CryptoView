package com.crypto.cryptoview.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crypto.cryptoview.presentation.component.SettingsScreen
import com.crypto.cryptoview.presentation.component.assetsOverview.AssetsOverviewScreen
import com.crypto.cryptoview.presentation.component.assetsOverview.AssetsOverviewViewModel
import com.crypto.cryptoview.presentation.component.holdingCoinView.HoldingCoinsViewModel
import com.crypto.cryptoview.presentation.component.holdingCoinView.HoldingDetailScreen
import com.crypto.cryptoview.presentation.component.holdingCoinView.HoldingsScreen
import com.crypto.cryptoview.ui.theme.CryptoViewTheme

@Composable
fun MainScreen(
    viewModel: AssetsOverviewViewModel,
    holdingsViewModel: HoldingCoinsViewModel
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFF1A1D2E),
                        contentColor = Color.White
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF5B7FFF),
                                selectedTextColor = Color(0xFF5B7FFF),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Holdings") },
                            label = { Text("Holdings") },
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF5B7FFF),
                                selectedTextColor = Color(0xFF5B7FFF),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings") },
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF5B7FFF),
                                selectedTextColor = Color(0xFF5B7FFF),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            ) { paddingValues ->
                when (selectedTab) {
                    0 -> AssetsOverviewScreen(
                        modifier = Modifier.padding(paddingValues),
                        viewModel = viewModel,
                        onNavigateToHoldings = { selectedTab = 1 }
                    )
                    1 -> HoldingsScreen(
                        modifier = Modifier.padding(paddingValues),
                        viewModel = holdingsViewModel,
                        onHoldingClick = { symbol -> navController.navigate("holding/$symbol") }
                    )
                    2 -> SettingsScreen(Modifier.padding(paddingValues))
                }
            }
        }
        composable("holding/{symbol}") { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
            HoldingDetailScreen(
                symbol = symbol,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    CryptoViewTheme {
        // 프리뷰용 임시 화면
        Text("Preview Mode", color = Color.White)
    }
}