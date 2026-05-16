# 🧮 Calculator 모듈 트러블슈팅

## 📋 목차
1. [아키텍처 개요](#아키텍처-개요)
2. [발생했던 주요 이슈](#발생했던-주요-이슈)
3. [해결 방법 및 설계 결정](#해결-방법-및-설계-결정)
4. [핵심 코드 설명](#핵심-코드-설명)
5. [포트폴리오용 요약](#포트폴리오용-요약)

---

## 🏗️ 아키텍처 개요

### 클래스 구조
```
BalanceCalculator (Interface)
    └── BaseBalanceCalculator (Abstract Class) - 공통 로직
            ├── UpbitBalanceCalculator - 국내 거래소 (KRW 기준)
            └── ForeignBalanceCalculator - 해외 거래소 (USDT→KRW 환산)

BalanceCalculatorFactory - 거래소별 Calculator 제공
ExchangeRateProvider - USDT/KRW 환율 조회
CalculateBalanceUseCase - UseCase (Calculator 조율 및 결과 통합)
```

### 데이터 흐름
```
API 응답 → UseCase → Factory → Calculator → HoldingData → UI
                         ↓
              ExchangeRateProvider (환율)
```

---

## 🔥 발생했던 주요 이슈

### 이슈 1: `Any` 타입 사용으로 인한 런타임 오류

#### ❌ 문제 상황
```kotlin
// 초기 구현 - Any 타입 사용
interface BalanceCalculator {
    fun calculate(balances: Any, tickers: Any): CalculationResult
}
```

**발생 오류:**
- `ClassCastException` 런타임 크래시
- 컴파일 타임에 타입 오류 감지 불가
- 코드 가독성 저하

#### ✅ 해결 방법
```kotlin
// 제네릭 타입 도입
interface BalanceCalculator<BALANCE, TICKER> {
    fun calculate(
        balances: List<BALANCE>, 
        tickers: TICKER, 
        usdtKrwRate: Double = 0.0
    ): CalculationResult
}
```

**결과:**
- 컴파일 타임 타입 체크
- IDE 자동완성 지원
- 안전한 타입 캐스팅

---

### 이슈 2: NullPointerException - 현재가 조회 실패

#### ❌ 문제 상황
```kotlin
// BaseBalanceCalculator.kt (Line 36)
val totalValue = amount * currentPrice!!  // NPE 발생!
```

**에러 로그:**
```
java.lang.NullPointerException
    at BaseBalanceCalculator.createHoldingData(BaseBalanceCalculator.kt:36)
    at ForeignBalanceCalculator.calculate(ForeignBalanceCalculator.kt:46)
```

**원인:**
- 해외 거래소 티커에서 일부 코인 가격 없음
- BGSC, POINT 등 소규모 코인은 USDT 페어 없음
- `tickers["${balance.asset}USDT"]` 반환값이 null

#### ✅ 해결 방법
```kotlin
// ForeignBalanceCalculator.kt
val holdings = balances
    .filter { it.free > 0 }
    .mapNotNull { balance ->  // map → mapNotNull 변경
        val priceUsdt = if (balance.asset == "USDT") {
            1.0
        } else {
            tickers["${balance.asset}USDT"]
        }
        
        // 가격이 없으면 null 반환 (자동 필터링)
        if (priceUsdt == null) return@mapNotNull null
        
        // ...계산 로직
    }
```

**핵심 포인트:**
- `map` → `mapNotNull` 변경으로 null 안전성 확보
- 가격 정보 없는 코인은 자동 제외
- 앱 크래시 방지

---

### 이슈 3: Gate.io 티커 조회 실패 (400 Bad Request)

#### ❌ 문제 상황
```
GET https://api.gateio.ws/api/v4/spot/tickers?currency_pair=XRP_USDT,BGSC_USDT,GT_USDT
<-- 400 Bad Request
```

**원인:**
- Gate.io API는 `currency_pair` 파라미터에 단일 페어만 허용
- 쉼표로 연결된 다중 페어 쿼리 불가

#### ✅ 해결 방법
```kotlin
// 개별 호출로 변경
val gateTickers = gateCurrencies.mapNotNull { currency ->
    getGateSpotTickers("${currency}_USDT")  // 개별 호출
        .getOrNull()
        ?.firstOrNull()
}
```

**추가 처리 - 티커 맵 변환:**
```kotlin
// CalculateBalanceUseCase.kt
val tickerMap = gateioTickers.associate { ticker ->
    // XRP_USDT -> XRPUSDT 형식으로 변환
    ticker.symbol.replace("_", "") to ticker.lastPrice
}
```

---

### 이슈 4: USDT/KRW 환율 조회 실패

#### ❌ 문제 상황
```
USDT/KRW Rate: 1300.0  // 기본값만 출력 (실제 환율 아님)
```

**원인:**
- 기존 API(`/v1/ticker`)는 **내 자산에 있는 코인만** 시세 조회
- USDT를 보유하지 않으면 환율 조회 불가

#### ✅ 해결 방법
```kotlin
// UbitMTickerRepositoryImpl.kt
// 마켓 리스트 생성 시 KRW-USDT를 항상 포함시킴
val markets = accounts
    .filter { it.currency != "KRW" && (it.balance.toDoubleOrNull() ?: 0.0) > 0 }
    .map { "KRW-${it.currency}" }
    .toMutableList()
    .apply {
        if (!contains("KRW-USDT")) {
            add("KRW-USDT")  // USDT는 항상 포함 → 환율 조회 보장
        }
    }
    .joinToString(",")
```

**핵심 포인트:**
- 새 API 추가 없이, 기존 `/v1/ticker` API 재활용
- 보유 여부와 관계없이 `KRW-USDT`를 마켓 리스트에 강제 추가
- 한 번의 API 호출로 보유 자산 시세 + USDT 환율 동시 조회

---

### 이슈 5: Factory에서 Calculator 인스턴스 공유 문제

#### ❌ 문제 상황
```kotlin
// 문제 코드
@Singleton
class BalanceCalculatorFactory @Inject constructor(
    private val foreignCalculator: ForeignBalanceCalculator  // 싱글톤!
) {
    fun getForeignCalculator(type: ExchangeType): ForeignBalanceCalculator {
        foreignCalculator.exchangeType = type  // 상태 공유 문제!
        return foreignCalculator
    }
}
```

**문제점:**
- 여러 거래소 동시 계산 시 `exchangeType` 덮어쓰기
- Gate.io 계산 중 Binance 타입으로 변경될 수 있음

#### ✅ 해결 방법
```kotlin
@Singleton
class BalanceCalculatorFactory @Inject constructor(
    private val upbitCalculator: UpbitBalanceCalculator,
    private val foreignCalculatorProvider: Provider<ForeignBalanceCalculator>  // Provider 사용
) {
    fun getForeignCalculator(exchangeType: ExchangeType): ForeignBalanceCalculator {
        // 매 호출마다 새 인스턴스 생성
        return foreignCalculatorProvider.get().apply { 
            this.exchangeType = exchangeType 
        }
    }
}
```

**핵심:**
- `Provider<T>` 사용으로 매번 새 인스턴스 생성
- 거래소별 독립적인 상태 유지
- 동시성 문제 해결

---

## 💡 핵심 코드 설명

### 1. 제네릭 인터페이스 설계
```kotlin
interface BalanceCalculator<BALANCE, TICKER> {
    data class CalculationResult(
        val totalValue: Double,
        val holdings: List<HoldingData>,
        val exchangeData: ExchangeData
    )
    
    fun calculate(
        balances: List<BALANCE>, 
        tickers: TICKER, 
        usdtKrwRate: Double = 0.0
    ): CalculationResult
}
```

**설계 의도:**
| 타입 파라미터 | 업비트 | 해외 거래소 |
|-------------|--------|-----------|
| `BALANCE` | `UpbitAccountBalance` | `ForeignBalance` |
| `TICKER` | `List<UpbitMarketTicker>` | `Map<String, Double>` |

### 2. 템플릿 메서드 패턴 (BaseBalanceCalculator)
```kotlin
abstract class BaseBalanceCalculator<BALANCE, TICKER> : BalanceCalculator<BALANCE, TICKER> {
    
    // 추상 프로퍼티 - 서브클래스에서 정의
    abstract val baseCurrency: String
    abstract val exchangeType: ExchangeType
    
    // 공통 로직 - 재사용
    protected fun createHoldingData(
        symbol: String,
        amount: Double,
        avgBuyPrice: Double,
        currentPrice: Double?,
        exchange: ExchangeType
    ): HoldingData {
        val totalValue = amount * (currentPrice ?: 0.0)
        val buyValue = amount * avgBuyPrice
        val change = totalValue - buyValue
        val changePercent = if (buyValue > 0) (change / buyValue) * 100 else 0.0
        // ...
    }
    
    protected fun emptyResult(): CalculationResult = /* 빈 결과 */
}
```

### 3. Factory 패턴 + Provider
```kotlin
@Singleton
class BalanceCalculatorFactory @Inject constructor(
    private val upbitCalculator: UpbitBalanceCalculator,
    private val foreignCalculatorProvider: Provider<ForeignBalanceCalculator>
) {
    fun getUpbitCalculator() = upbitCalculator
    
    fun getForeignCalculator(exchangeType: ExchangeType): ForeignBalanceCalculator {
        return foreignCalculatorProvider.get().apply { 
            this.exchangeType = exchangeType 
        }
    }
}
```

---

## 📝 포트폴리오용 요약

### 🎯 문제 정의
> 국내(업비트)와 해외(Gate.io) 거래소의 잔고 계산 로직이 달라 **코드 중복**이 발생하고, 
> **타입 안전성**이 보장되지 않아 런타임 오류가 빈번했습니다.

### 💡 해결 방안
1. **제네릭 인터페이스** 도입으로 타입 안전성 확보
2. **템플릿 메서드 패턴**으로 공통 로직 추출 (90% 재사용)
3. **Factory + Provider 패턴**으로 거래소별 인스턴스 관리
4. **ExchangeRateProvider** 분리로 환율 로직 단일화

### 📊 개선 효과
| 항목 | Before | After |
|------|--------|-------|
| 타입 안전성 | `Any` 캐스팅 | 컴파일 타임 체크 |
| 코드 중복 | 거래소별 별도 구현 | 90% 공통 로직 재사용 |
| 확장성 | 거래소 추가 시 전체 수정 | Calculator만 추가 |
| 테스트 | Mock 어려움 | 인터페이스 기반 쉬운 Mock |

### 🔑 핵심 키워드
- Clean Architecture (Domain Layer UseCase)
- Generic Interface + Abstract Class
- Factory Pattern + Dagger Provider
- Null Safety (mapNotNull)
- Single Responsibility Principle

---

## 📂 관련 파일
```
domain/usecase/calculator/
├── BalanceCalculator.kt          # 인터페이스 정의
├── BaseBalanceCalculator.kt      # 추상 클래스 (공통 로직)
├── UpbitBalanceCalculator.kt     # 국내 거래소 구현
├── ForeignBalanceCalculator.kt   # 해외 거래소 구현
├── BalanceCalculatorFactory.kt   # Factory 패턴
├── ExchangeRateProvider.kt       # 환율 제공자
└── CalculateBalanceUseCase.kt    # UseCase (조율자)
```
