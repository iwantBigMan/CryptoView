package com.crypto.cryptoview.presentation.component.holdingCoinView.preview

import androidx.compose.ui.graphics.Color
import com.crypto.cryptoview.ui.theme.AVAXRed
import com.crypto.cryptoview.ui.theme.BTCOrange
import com.crypto.cryptoview.ui.theme.ETHPurple
import com.crypto.cryptoview.ui.theme.SOLPurple

// 거래소별 보유 정보
data class ExchangeHolding(
    val exchangeName: String,
    val amount: Double,
    val symbol: String,
    val profitPercent: Double
)

// 코인 보유 정보
data class CoinHolding(
    val symbol: String,
    val displayName: String,
    val iconColor: Color,
    val totalValueKRW: Long,
    val profitKRW: Long,
    val kimchiPremium: Double? = null,
    val exchangeHoldings: List<ExchangeHolding>
)

// 필터 타입
enum class SortType {
    VALUE, PROFIT, SYMBOL
}

// 샘플 데이터
val sampleHoldings = listOf(
    CoinHolding(
        symbol = "BTC",
        displayName = "BTC",
        iconColor = BTCOrange,
        totalValueKRW = 0,
        profitKRW = 0,
        kimchiPremium = null,
        exchangeHoldings = listOf(
            ExchangeHolding("Upbit", 0.5, "BTC", 4.12),
            ExchangeHolding("Binance", 0.3, "BTC", 7.26),
            ExchangeHolding("Gate.io", 0.2, "BTC", 6.03),
            ExchangeHolding("Bybit", 0.15, "BTC", 4.06)
        )
    ),
    CoinHolding(
        symbol = "ETH",
        displayName = "ETH",
        iconColor = ETHPurple,
        totalValueKRW = 51351900,
        profitKRW = 3846300,
        kimchiPremium = 1.76,
        exchangeHoldings = listOf(
            ExchangeHolding("Upbit", 5.0, "ETH", 7.14),
            ExchangeHolding("Binance", 3.0, "ETH", 8.06),
            ExchangeHolding("Bybit", 2.0, "ETH", 10.82),
            ExchangeHolding("Gate.io", 1.5, "ETH", 7.85)
        )
    ),
    CoinHolding(
        symbol = "SOL",
        displayName = "SOL",
        iconColor = SOLPurple,
        totalValueKRW = 23291400,
        profitKRW = 3214200,
        kimchiPremium = null,
        exchangeHoldings = listOf(
            ExchangeHolding("Binance", 50.0, "SOL", 15.86),
            ExchangeHolding("Bybit", 30.0, "SOL", 19.01),
            ExchangeHolding("Gate.io", 25.0, "SOL", 12.84)
        )
    ),
    CoinHolding(
        symbol = "AVAX",
        displayName = "AVAX",
        iconColor = AVAXRed,
        totalValueKRW = 9115920,
        profitKRW = 694320,
        kimchiPremium = null,
        exchangeHoldings = listOf(
            ExchangeHolding("Bybit", 100.0, "AVAX", 10.00),
            ExchangeHolding("Gate.io", 80.0, "AVAX", 6.11)
        )
    )
)