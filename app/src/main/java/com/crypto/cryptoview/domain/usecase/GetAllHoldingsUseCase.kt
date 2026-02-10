package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.AggregatedHolding
import com.crypto.cryptoview.domain.model.HoldingData
import com.crypto.cryptoview.domain.usecase.calculator.BalanceCalculator
import com.crypto.cryptoview.domain.usecase.calculator.CalculateBalanceUseCase
import com.crypto.cryptoview.domain.usecase.calculator.ExchangeRateProvider
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotBalancesUseCase
import com.crypto.cryptoview.domain.usecase.gate.GetGateSpotTickersUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitAccountBalancesUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitMTickerUseCase
import com.crypto.cryptoview.domain.usecase.upbit.GetUpbitTickerAllUseCase
import com.crypto.cryptoview.domain.util.HoldingAggregator
import javax.inject.Inject

/**
 * 전체 보유 자산 조회 유스케이스
 *
 * 책임:
 * - 모든 거래소 데이터 조회 조율
 * - 잔고 계산 위임
 * - 결과 통합 및 정규화
 *
 * ViewModel은 이 UseCase만 호출하면 됨
 */
class GetAllHoldingsUseCase @Inject constructor(
    private val getUpbitAccountBalance: GetUpbitAccountBalancesUseCase,
    private val getUpbitMarketTicker: GetUpbitMTickerUseCase,
    private val getUpbitTickerAll: GetUpbitTickerAllUseCase,
    private val getGateSpotBalances: GetGateSpotBalancesUseCase,
    private val getGateSpotTickers: GetGateSpotTickersUseCase,
    private val calculateBalanceUseCase: CalculateBalanceUseCase,
    private val exchangeRateProvider: ExchangeRateProvider
) {
    /**
     * 전체 보유 자산 조회
     * @param minValue 최소 금액 필터 (기본값: 1원)
     * @return 조회 결과
     */
    suspend operator fun invoke(minValue: Double = 1.0): Result<HoldingsResult> = runCatching {
        // 1. 모든 거래소 데이터 병렬 로드
        val upbitBalances = getUpbitAccountBalance().getOrElse { emptyList() }
        val upbitTickers = getUpbitMarketTicker().getOrElse { emptyList() }
        val upbitTickerAll = getUpbitTickerAll().getOrElse { emptyList() }
        val gateBalances = getGateSpotBalances().getOrElse { emptyList() }
        val gateTickers = getGateSpotTickers().getOrElse { emptyList() }

        // 2. USDT/KRW 환율 조회
        val usdtKrwRate = exchangeRateProvider.getUsdtKrwRate(upbitTickers)

        // 3. 전체 거래소 잔고 계산
        val calculationResult = calculateBalanceUseCase.calculateAll(
            upbitBalances = upbitBalances,
            upbitTickers = upbitTickers,
            upbitAllTickers = upbitTickerAll,
            gateioBalances = gateBalances,
            gateioTickers = gateTickers
        )

        // 4. 홀딩 데이터 추출 및 필터링
        val allHoldings = calculationResult.results
            .flatMap { it.holdings }
            .filter { it.totalValue > minValue }
            .sortedByDescending { it.totalValue }

        // 5. 심볼 기준 통합 (정규화)
        val aggregatedHoldings = HoldingAggregator.aggregateFiltered(
            holdings = calculationResult.results.flatMap { it.holdings },
            minValue = minValue
        )

        // 6. 결과 반환
        HoldingsResult(
            allHoldings = allHoldings,
            aggregatedHoldings = aggregatedHoldings,
            exchangeResults = calculationResult.results,
            totalValue = calculationResult.totalValue,
            usdtKrwRate = usdtKrwRate
        )
    }

    /**
     * 보유 자산 조회 결과
     *
     * @property allHoldings 거래소별 개별 홀딩 (디테일용)
     * @property aggregatedHoldings 심볼 기준 통합 홀딩 (요약용)
     * @property exchangeResults 거래소별 계산 결과
     * @property totalValue 전체 자산 가치 (KRW)
     * @property usdtKrwRate 사용된 USDT/KRW 환율
     */
    data class HoldingsResult(
        val allHoldings: List<HoldingData>,
        val aggregatedHoldings: List<AggregatedHolding>,
        val exchangeResults: List<BalanceCalculator.CalculationResult>,
        val totalValue: Double,
        val usdtKrwRate: Double
    )
}
