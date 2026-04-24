package com.crypto.cryptoview.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Custom App Colors
val BackgroundDark = Color(0xFF0A0E27)
val SurfaceDark = Color(0xFF1A1F3A)
val PrimaryBlue = Color(0xFF4E7FFF)
val PrimaryPurple = Color(0xFF8B5CF6)

// Chart Colors
val BinanceOrange = Color(0xFFF0B90B)
val GateIOPurple = Color(0xFF8B5CF6)
val UpbitBlue = Color(0xFF3B82F6)
val BybitPink = Color(0xFFEC4899)

// Status Colors
val PositiveGreen = Color(0xFF10B981)
val NegativeRed = Color(0xFFEF4444)
val NeutralGray = Color(0xFF6B7280)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9CA3AF)
val TextTertiary = Color(0xFF6B7280)

// Kimchi Premium (김프) Colors
val KimchiPremiumBg = Color(0xFF22C55E)  // 초록색 배경
val KimchiPremiumText = Color(0xFFFFFFFF)  // 흰색 텍스트

// Coin Symbol Background Colors
val BTCOrange = Color(0xFFF7931A)
val ETHPurple = Color(0xFF627EEA)
val SOLPurple = Color(0xFF9945FF)
val AVAXRed = Color(0xFFE84142)

// Search Bar
val SearchBarBg = Color(0xFF1E2340)
val SearchBarText = Color(0xFF6B7280)

// Filter Chip Colors
val ChipSelectedBg = Color(0xFF3B82F6)
val ChipUnselectedBg = Color(0xFF374151)

// Value Colors (원화 표시)
val ValuePrimary = Color(0xFFFFFFFF)
val ValuePositive = Color(0xFF34D399)  // 연한 초록 (+₩3,846,300)

// Screen Colors (Login/Common)
val BackgroundPrimary = Color(0xFF071029)
val CardBackground = Color(0xFF0F1720)
val AccentBlue = Color(0xFF5B7FFF)
val ErrorColor = Color(0xFFEF4444)
val PositiveColor = Color(0xFF25D366)
val NegativeColor = Color(0xFFEF5350)

// ============================
// 테마 팔레트 (다크 / 라이트)
// ============================

data class AppColors(
    // 배경
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val cardBackground: Color,
    val surfaceVariant: Color,

    // 텍스트
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,

    // 액션
    val accentBlue: Color,

    // 상태
    val positive: Color,
    val negative: Color,
    val error: Color,

    // 입력/검색
    val searchBarBg: Color,
    val chipSelected: Color,
    val chipUnselected: Color,

    // 테마 여부
    val isDark: Boolean
)

val DarkAppColors = AppColors(
    backgroundPrimary  = Color(0xFF071029),
    backgroundSecondary = Color(0xFF0A0E27),
    cardBackground     = Color(0xFF0F1720),
    surfaceVariant     = Color(0xFF1A1D2E),
    textPrimary        = Color(0xFFFFFFFF),
    textSecondary      = Color(0xFF9CA3AF),
    textTertiary       = Color(0xFF6B7280),
    accentBlue         = Color(0xFF5B7FFF),
    positive           = Color(0xFF25D366),
    negative           = Color(0xFFEF5350),
    error              = Color(0xFFEF4444),
    searchBarBg        = Color(0xFF1E2340),
    chipSelected       = Color(0xFF3B82F6),
    chipUnselected     = Color(0xFF374151),
    isDark             = true
)

val LightAppColors = AppColors(
    backgroundPrimary  = Color(0xFFF0F4FF),
    backgroundSecondary = Color(0xFFE8EDF8),
    cardBackground     = Color(0xFFFFFFFF),
    surfaceVariant     = Color(0xFFEAEFF8),
    textPrimary        = Color(0xFF0D1117),
    textSecondary      = Color(0xFF4B5563),
    textTertiary       = Color(0xFF9CA3AF),
    accentBlue         = Color(0xFF3B6FFF),
    positive           = Color(0xFF16A34A),
    negative           = Color(0xFFDC2626),
    error              = Color(0xFFDC2626),
    searchBarBg        = Color(0xFFDDE3F0),
    chipSelected       = Color(0xFF3B82F6),
    chipUnselected     = Color(0xFFD1D5DB),
    isDark             = false
)
