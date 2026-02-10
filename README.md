# CryptoView

> 🪙 **멀티 거래소 암호화폐 자산 통합 관리 앱**

여러 거래소(Upbit, Gate.io 등)의 API를 연동해 개인 보유 자산을 한눈에 확인할 수 있는 안드로이드 앱입니다.  
거래소별 자산 요약, 상위 보유 코인, 코인 상세 정보를 확인하고, USDT ↔ KRW 자동 환산을 지원합니다.

---

## 📱 스크린샷

| 홈 대시보드 | 보유 코인 목록 | 코인 상세 |
|:---:|:---:|:---:|
| 전체 자산 요약 | LazyColumn 기반 목록 | 거래소별 포지션 |

---

## ✨ 주요 기능

### 1. 로그인 / 거래소 연동
- 업비트(필수) + 해외 거래소(Gate.io) 동시 연동
- API Key/Secret 입력 → 실시간 검증 → 암호화 저장 (DataStore에 AES/GCM으로 암호화 — Android Keystore 사용, `SecureStorage` 유틸리티 사용)
- 연동 상태: Idle / Validating / Success / Error
- 연동 실패 시 다이얼로그로 오류 안내

### 2. 홈 대시보드
- **전체 자산 요약**: 총 평가금액, 총 수익률, 변동금액
- **거래소별 요약**: 도넛 차트로 시각화 (0원인 거래소 자동 제외)
- **Top 5 보유코인**: 평가금액 상위 5개 코인 표시

### 3. 보유 코인 리스트 (Holdings)
- 코인별 심볼, 수량, 평가금액, 수익률 표시
- 정렬 (평가금액/수익률), 검색 기능
- `LazyColumn`으로 성능 최적화

### 4. 코인 상세 (CoinDetail)
- 특정 코인의 거래소별 합산 포지션
- 거래소별 보유량·평균단가·평가금액 확인

### 5. 설정
- 테마 설정 (다크 모드 기본)
- API 키 관리 (추가/수정/삭제)

---

## 🏗️ 아키텍처

```
Clean Architecture + MVVM
├── presentation/   # UI Layer (Compose, ViewModel)
├── domain/         # Business Logic (UseCase, Repository Interface, Model)
└── data/           # Data Layer (API, DTO, Repository Implementation)
```

### 주요 설계 패턴
- **Factory Pattern**: 거래소별 Calculator 분기 (`BalanceCalculatorFactory`)
- **Strategy Pattern**: 국내/해외 잔고 계산 로직 분리 (`UpbitBalanceCalculator`, `ForeignBalanceCalculator`)
- **Repository Pattern**: 데이터 소스 추상화

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|:---|:---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose, Material3 |
| **DI** | Dagger Hilt |
| **Network** | Retrofit, OkHttp, Kotlinx Serialization |
| **Storage** | DataStore (AES/GCM 암호화, Android Keystore 사용 via `SecureStorage`) |
| **Database** | Room (예정) |
| **Async** | Kotlin Coroutines, Flow |
| **Auth** | JWT (업비트), HMAC-SHA512 (Gate.io) |
| **Min SDK** | 23 (Android 6.0) |
| **Target SDK** | 36 |

---

## 📁 프로젝트 구조

```
app/src/main/java/com/crypto/cryptoview/
├── CryptoViewApplication.kt
├── data/
│   ├── local/
│   │   ├── CredentialsManager.kt      # API 키 암호화 저장/조회
│   │   ├── CredentialsProvider.kt     # 자격증명 제공 인터페이스
│   │   └── SecureStorage.kt           # 암호화 스토리지
│   ├── remote/
│   │   ├── api/
│   │   │   ├── UpbitApi.kt            # 업비트 REST API
│   │   │   └── GateIOApi.kt           # Gate.io REST API
│   │   ├── dto/                       # 데이터 전송 객체
│   │   └── interceptor/               # 인증 인터셉터 (JWT, HMAC)
│   └── repository/                    # Repository 구현체
├── domain/
│   ├── model/
│   │   ├── ExchangeType.kt            # 거래소 타입 enum
│   │   ├── HoldingData.kt             # 보유 자산 모델
│   │   ├── AggregatedHolding.kt       # 심볼별 통합 자산
│   │   └── gate/                      # Gate.io 도메인 모델
│   ├── repository/                    # Repository 인터페이스
│   ├── usecase/
│   │   ├── upbit/                     # 업비트 UseCase
│   │   ├── gate/                      # Gate.io UseCase
│   │   ├── calculator/
│   │   │   ├── BalanceCalculator.kt       # 계산기 인터페이스
│   │   │   ├── BalanceCalculatorFactory.kt
│   │   │   ├── UpbitBalanceCalculator.kt  # 업비트 잔고 계산
│   │   │   ├── ForeignBalanceCalculator.kt # 해외 잔고 계산 (USDT→KRW)
│   │   │   └── ExchangeRateProvider.kt    # 환율 제공자
│   │   └── GetAllHoldingsUseCase.kt   # 통합 보유 자산 조회
│   └── util/
├── presentation/
│   ├── login/                         # 로그인/연동 화면
│   ├── main/                          # 메인 네비게이션
│   └── component/
│       ├── assetsOverview/            # 홈 대시보드
│       ├── holdingCoinView/           # 코인 상세
│       └── holdingColins/             # 보유 코인 목록
├── di/                                # Hilt 모듈
├── ui/                                # 테마, 컬러
└── util/                              # 유틸리티
```

---

## 🔐 보안

- **API Key 암호화**: DataStore에 AES/GCM 방식으로 암호화하여 저장 (Android Keystore의 키로 암/복호화; 프로젝트 내 `SecureStorage` 사용)
- **HTTPS 통신**: 모든 API 요청 암호화
- **자동 매매 미지원**: View 전용 앱 (읽기만 가능)
- **민감 정보 로그 제외**: 프로덕션 빌드에서 키 로깅 차단

---

## 📋 진행 상황

### ✅ 완료

| 기능 | 상태 | 설명 |
|:---|:---:|:---|
| 홈 대시보드 | ✅ | 전체 자산 요약, 도넛 차트, Top 5 Holdings |
| 업비트 연동 | ✅ | API 연동, 잔고/시세 조회, JWT 인증 |
| Gate.io 연동 | ✅ | 현물 잔고/시세 조회, HMAC 인증 |
| USDT→KRW 환산 | ✅ | 업비트 USDT/KRW 시세 기준 자동 환산 |
| 보유 코인 목록 | ✅ | LazyColumn, 정렬, 검색, 0원 필터링 |
| 코인 상세 | ✅ | 거래소별 포지션 확인 |
| 로그인/연동 UI | ✅ | 업비트(필수) + 해외 거래소 드롭다운 |
| API 키 암호화 | ✅ | EncryptedSharedPreferences 적용 |
| 심볼 정규화 | ✅ | 다중 거래소 동일 코인 통합 |

### 🚧 진행 중 / 예정

| 기능 | 상태 | 설명 |
|:---|:---:|:---|
| Gate.io 선물 | 🚧 | 선물 포지션 조회 구현 중 |
| Settings | 📝 | 테마, 통화 설정 |
| 알림 | 📝 | 목표가 도달, 수익률 알림 |
| 백그라운드 동기화 | 📝 | WorkManager 기반 |
| 거래소 딥링크 | 📝 | 자산 클릭 시 거래소 앱 이동 |

---

## 🚀 시작하기

### 필수 조건
- Android Studio Hedgehog 이상
- JDK 17
- Android SDK 36

### 설치

```bash
git clone https://github.com/your-repo/cryptoview.git
cd cryptoview
```

### 빌드

```bash
./gradlew assembleDebug
```

---

## 📄 라이선스

This project is for personal/educational use only.
