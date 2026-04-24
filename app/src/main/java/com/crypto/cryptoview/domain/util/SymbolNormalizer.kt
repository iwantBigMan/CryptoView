package com.crypto.cryptoview.domain.util

/**
 * 거래소별로 다른 심볼 표기를 정규화하는 유틸리티
 * 예: USDT, usdt → USDT / BTC, btc → BTC
 *
 * 일부 거래소는 다른 표기 사용:
 * - Upbit: XRP, BTC (대문자)
 * - Gate.io: xrp, btc (소문자 가능) / 일부 토큰은 다른 이름 사용
 */
object SymbolNormalizer {

    /**
     * 거래소별 심볼 매핑 테이블
     * key: 거래소에서 사용하는 심볼 (소문자)
     * value: 정규화된 심볼 (대문자)
     */
    private val symbolMappings = mapOf(
        // 스테이블코인 변형
        "usdt" to "USDT",
        "usdc" to "USDC",
        "busd" to "BUSD",
        "tusd" to "TUSD",
        "dai" to "DAI",

        // 일부 거래소에서 다른 이름 사용하는 경우
        "xbt" to "BTC",           // 일부 거래소에서 BTC를 XBT로 표기
        "xdg" to "DOGE",          // 일부에서 DOGE를 XDG로 표기
        "str" to "XLM",           // Stellar 옛날 표기

        // 래핑/브릿지 토큰 → 원본으로 매핑 (선택사항)
        // "wbtc" to "BTC",       // 필요시 활성화
        // "weth" to "ETH",
    )

    /**
     * 심볼을 정규화된 형태로 변환
     *
     * @param symbol 원본 심볼 (거래소에서 받은 그대로)
     * @return 정규화된 심볼 (대문자, 매핑 적용)
     */
    fun normalize(symbol: String): String {
        val lowerSymbol = symbol.trim().lowercase()

        // 매핑 테이블에 있으면 매핑된 값 반환
        symbolMappings[lowerSymbol]?.let { return it }

        // 없으면 대문자로 변환하여 반환
        return symbol.trim().uppercase()
    }

    /**
     * 두 심볼이 같은 코인인지 비교
     *
     * @param symbol1 첫 번째 심볼
     * @param symbol2 두 번째 심볼
     * @return 같은 코인이면 true
     */
    fun isSameAsset(symbol1: String, symbol2: String): Boolean {
        return normalize(symbol1) == normalize(symbol2)
    }

    /**
     * 심볼 리스트를 정규화하고 중복 제거
     */
    fun normalizeAndDistinct(symbols: List<String>): List<String> {
        return symbols.map { normalize(it) }.distinct()
    }
}
