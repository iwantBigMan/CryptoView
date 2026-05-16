# 📁 CryptoView 프로젝트 구조

## 🏗️ 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  (UI, ViewModel, UiState)                                   │
├─────────────────────────────────────────────────────────────┤
│                      Domain Layer                            │
│  (UseCase, Repository Interface, Model)                     │
├─────────────────────────────────────────────────────────────┤
│                       Data Layer                             │
│  (Repository Impl, API, DTO, Local Storage)                 │
└─────────────────────────────────────────────────────────────┘
```

**Clean Architecture + MVVM 패턴 적용**

---

## 📂 전체 디렉토리 구조

```
com.crypto.cryptoview/
│
├── 📄 CryptoViewApplication.kt          # Hilt Application
│
├── 📁 data/                             # 🔴 Data Layer
│   ├── 📁 local/                        # 로컬 저장소
│   │   ├── CredentialsManager.kt        # API 키 관리 (저장/조회/삭제)
│   │   ├── CredentialsProvider.kt       # 키 제공자 인터페이스
│   │   └── SecureStorage.kt             # AES 암호화 저장소
│   │
│   ├── 📁 remote/                       # 원격 API
│   │   ├── 📁 api/                      # Retrofit API 인터페이스
│   │   │   ├── AuthGateApi.kt           # Gate.io 인증 테스트 API
│   │   │   ├── AuthUpbitApi.kt          # 업비트 인증 테스트 API
│   │   │   ├── GateIOApi.kt             # Gate.io 현물/선물 API
│   │   │   └── UpbitApi.kt              # 업비트 API
│   │   │
│   │   ├── 📁 dto/                      # Data Transfer Objects
│   │   │   ├── 📁 gateio/               # Gate.io DTO
│   │   │   │   ├── GateSpotBalanceDto.kt
│   │   │   │   ├── GateSpotTickerDto.kt
│   │   │   │   ├── GateFuturesAccountDto.kt
│   │   │   │   ├── GateFuturesPositionDto.kt
│   │   │   │   └── GateFuturesTickerDto.kt
│   │   │   │
│   │   │   └── 📁 upbit/                # 업비트 DTO
│   │   │       ├── UpbitAccountBalanceDto.kt
│   │   │       ├── UpbitMarketTickerDto.kt
│   │   │       └── UpbitTickerAllDto.kt
│   │   │
│   │   └── 📁 interceptor/              # OkHttp 인터셉터
│   │       ├── GateAuthInterceptor.kt   # Gate.io HMAC 서명
│   │       └── UpbitAuthInterceptor.kt  # 업비트 JWT 서명
│   │
│   └── 📁 repository/                   # Repository 구현체
│       ├── 📁 auth/                     # 인증 Repository
│       │   └── AuthRepositoryImpl.kt
│       ├── 📁 gateRepoImpl/             # Gate.io Repository
│       │   ├── GateSpotRepositoryImpl.kt
│       │   └── GateFuturesRepositoryImpl.kt
│       └── 📁 upbitRepoImpl/            # 업비트 Repository
│           ├── UpbitAssetRepositoryImpl.kt
│           └── UpbitMarketRepositoryImpl.kt
│
├── 📁 domain/                           # 🟢 Domain Layer
│   ├── 📁 model/                        # 도메인 모델
│   │   ├── AggregatedHolding.kt         # 심볼 기준 통합 자산
│   │   ├── CommonBalance.kt             # 공통 잔고 모델
│   │   ├── ExchangeCredentials.kt       # 거래소 인증 정보
│   │   ├── ExchangeData.kt              # 거래소별 자산 데이터
│   │   ├── ExchangeHoldingDetail.kt     # 거래소별 보유 상세
│   │   ├── ExchangeType.kt              # 거래소 타입 Enum
│   │   ├── ForeignBalance.kt            # 해외 거래소 잔고
│   │   ├── HoldingData.kt               # 보유 자산 데이터
│   │   ├── UpbitAccountBalance.kt       # 업비트 잔고 모델
│   │   ├── UpbitMarketTicker.kt         # 업비트 시세 모델
│   │   ├── UpbitTickerAll.kt            # 업비트 전체 시세
│   │   │
│   │   └── 📁 gate/                     # Gate.io 도메인 모델
│   │       ├── GateSpotBalance.kt
│   │       ├── GateSpotTicker.kt
│   │       ├── GateFuturesAccount.kt
│   │       ├── GateFuturesPosition.kt
│   │       └── GateFuturesTicker.kt
│   │
│   ├── 📁 repository/                   # Repository 인터페이스
│   │   ├── AuthRepository.kt            # 인증 Repository
│   │   ├── GateRepository.kt            # Gate.io Repository
│   │   └── UpbitAssetRepository.kt      # 업비트 Repository
│   │
│   ├── 📁 usecase/                      # UseCase
│   │   ├── 📁 auth/                     # 인증 UseCase
│   │   │   ├── ValidateUpbitCredentialsUseCase.kt
│   │   │   └── ValidateGateCredentialsUseCase.kt
│   │   │
│   │   ├── 📁 calculator/               # 잔고 계산 UseCase ⭐
│   │   │   ├── BalanceCalculator.kt          # 인터페이스
│   │   │   ├── BaseBalanceCalculator.kt      # 추상 클래스 (공통 로직)
│   │   │   ├── UpbitBalanceCalculator.kt     # 업비트 계산기
│   │   │   ├── ForeignBalanceCalculator.kt   # 해외 거래소 계산기
│   │   │   ├── BalanceCalculatorFactory.kt   # 팩토리 패턴
│   │   │   ├── ExchangeRateProvider.kt       # 환율 제공자
│   │   │   └── CalculateBalanceUseCase.kt    # 통합 UseCase
│   │   │
│   │   ├── 📁 upbit/                    # 업비트 UseCase
│   │   │   ├── GetUpbitAccountBalancesUseCase.kt
│   │   │   ├── GetUpbitMTickerUseCase.kt
│   │   │   └── GetUpbitTickerAllUseCase.kt
│   │   │
│   │   ├── 📁 gate/                     # Gate.io UseCase
│   │   │   ├── GetGateSpotBalancesUseCase.kt
│   │   │   ├── GetGateSpotTickersUseCase.kt
│   │   │   ├── GetGateFuturesAccountUseCase.kt
│   │   │   ├── GetGateFuturesPositionsUseCase.kt
│   │   │   └── GetGateFuturesTickersUseCase.kt
│   │   │
│   │   ├── GetAllHoldingsUseCase.kt     # 전체 보유 자산 조회
│   │   └── GetExchangeHoldingDetailsUseCase.kt  # 거래소별 상세
│   │
│   └── 📁 util/                         # 도메인 유틸
│       └── SymbolNormalizer.kt          # 심볼 정규화
│
├── 📁 presentation/                     # 🔵 Presentation Layer
│   ├── 📁 login/                        # 로그인 화면
│   │   ├── LoginScreen.kt               # Compose UI
│   │   ├── LoginViewModel.kt            # ViewModel
│   │   └── LoginUiState.kt              # UI 상태
│   │
│   ├── 📁 main/                         # 메인 화면
│   │   ├── MainActivity.kt              # Activity
│   │   └── MainScreen.kt                # 네비게이션 컨테이너
│   │
│   └── 📁 component/                    # 화면 컴포넌트
│       ├── 📁 assetsOverview/           # 홈 (자산 개요)
│       │   ├── AssetsOverviewScreen.kt  # 홈 화면 UI
│       │   ├── AssetsOverviewViewModel.kt
│       │   ├── AssetOverViewState.kt    # UI 상태
│       │   └── 📁 chart/                # 도넛 차트
│       │       └── DonutChart.kt
│       │
│       ├── 📁 holdingCoinView/          # 보유 코인 화면
│       │   ├── HoldingsScreen.kt        # 목록 화면
│       │   ├── HoldingsUiState.kt
│       │   ├── HoldingCoinsViewModel.kt
│       │   ├── HoldingDetailScreen.kt   # 상세 화면
│       │   ├── HoldingDetailViewModel.kt
│       │   ├── HoldingDetailUiState.kt
│       │   └── 📁 preview/              # 프리뷰용
│       │
│       ├── 📁 holdingColins/            # (Legacy)
│       │   └── 📁 preview/
│       │
│       └── SettingsScreen.kt            # 설정 화면
│
├── 📁 di/                               # 🟡 Dependency Injection
│   ├── NetworkModule.kt                 # Retrofit, OkHttp 설정
│   └── RepositoryModule.kt              # Repository 바인딩
│
├── 📁 util/                             # 🟠 유틸리티
│   ├── sha512Hex.kt                     # SHA512 해시
│   └── 📁 authHelper/                   # 인증 헬퍼
│       ├── UpbitAuthHelper.kt           # 업비트 JWT 생성
│       └── GateIOAuthHelper.kt          # Gate.io HMAC 생성
│
└── 📁 ui/                               # UI 테마
    └── theme/
```

---

## 🔄 데이터 흐름

### 1. 자산 조회 흐름
```
AssetsOverviewScreen
        │
        ▼
AssetsOverviewViewModel
        │
        ├─► GetUpbitAccountBalancesUseCase ─► UpbitAssetRepository ─► UpbitApi
        ├─► GetUpbitTickerAllUseCase ────────► UpbitMarketRepository ─► UpbitApi
        ├─► GetGateSpotBalancesUseCase ──────► GateRepository ─► GateIOApi
        │
        ▼
CalculateBalanceUseCase
        │
        ├─► BalanceCalculatorFactory
        │       ├─► UpbitBalanceCalculator (KRW 계산)
        │       └─► ForeignBalanceCalculator (USDT→KRW 환산)
        │
        └─► ExchangeRateProvider (USDT/KRW 환율)
        │
        ▼
    UI 상태 업데이트
```

### 2. 로그인 흐름
```
LoginScreen
     │
     ▼
LoginViewModel
     │
     ├─► ValidateUpbitCredentialsUseCase ─► AuthRepository ─► AuthUpbitApi
     ├─► ValidateGateCredentialsUseCase ──► AuthRepository ─► AuthGateApi
     │
     ▼
CredentialsManager (AES 암호화 저장)
     │
     ▼
SecureStorage (DataStore)
```

---

## 🔑 핵심 설계 패턴

### 1. Clean Architecture
| 계층 | 역할 | 의존성 방향 |
|------|------|------------|
| Presentation | UI, ViewModel | → Domain |
| Domain | UseCase, Model | 없음 (독립) |
| Data | API, Repository 구현 | → Domain |

### 2. Calculator 패턴
```
BalanceCalculator (Interface)
        │
BaseBalanceCalculator (Abstract - 공통 로직)
        │
   ┌────┴────┐
   ▼         ▼
Upbit    Foreign
Calculator Calculator
```

### 3. Factory + Provider 패턴
```kotlin
BalanceCalculatorFactory
    ├── getUpbitCalculator()      → 싱글톤
    └── getForeignCalculator()    → Provider (매번 새 인스턴스)
```

### 4. Repository 패턴
```
Domain Layer          Data Layer
     │                    │
Repository ◄─────── RepositoryImpl
(Interface)              │
                    ┌────┴────┐
                    ▼         ▼
                  API       Local
```

---

## 🔐 보안 구조

```
API Key 입력
     │
     ▼
ValidateCredentialsUseCase (키 검증)
     │
     ▼
CredentialsManager
     │
     ▼
SecureStorage (AES-256 암호화)
     │
     ▼
DataStore (암호화된 상태로 저장)
     │
     ▼
Interceptor (요청 시 복호화 후 서명 생성)
```

---

## 📊 주요 모델 관계

### 자산 데이터 모델
```
HoldingData (개별 자산)
     │
     ├── symbol: String
     ├── balance: Double
     ├── totalValue: Double (KRW)
     ├── change: Double
     ├── changePercent: Double
     └── exchange: ExchangeType

AggregatedHolding (심볼 기준 통합)
     │
     ├── symbol: String
     ├── totalValue: Double
     └── exchangeDetails: List<ExchangeHoldingDetail>
```

### 거래소 타입
```kotlin
enum class ExchangeType {
    UPBIT,    // 업비트 (국내)
    BINANCE,  // 바이낸스
    BYBIT,    // 바이빗
    GATEIO    // 게이트아이오
}
```

---

## 📱 화면 구성

```
MainActivity
     │
     └── MainScreen (Navigation)
              │
              ├── Tab 0: AssetsOverviewScreen (홈)
              │           └── DonutChart (거래소별 비율)
              │
              ├── Tab 1: HoldingsScreen (보유 코인)
              │           └── HoldingDetailScreen (상세)
              │
              └── Tab 2: SettingsScreen (설정)
                          └── 로그아웃
```

---

## 📦 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Kotlin |
| UI | Jetpack Compose |
| DI | Hilt |
| Network | Retrofit2, OkHttp |
| Async | Coroutines, Flow |
| Storage | DataStore + AES |
| Architecture | Clean Architecture + MVVM |

---

## 📄 관련 문서
- [Calculator 트러블슈팅](./CALCULATOR_TROUBLESHOOTING.md)
- [로그인 구현](../LOGIN_IMPLEMENTATION.md)
- [README](../README.md)
