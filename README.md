# CryptoView

> 🪙 **멀티 거래소 암호화폐 자산 통합 관리 앱**

Google 계정으로 로그인 후, 여러 거래소(Upbit, Gate.io 등)의 API를 연동해 개인 보유 자산을 한눈에 확인할 수 있는 안드로이드 앱입니다.  
거래소별 자산 요약, 상위 보유 코인, 코인 상세 정보를 확인하고, USDT ↔ KRW 자동 환산을 지원합니다.

---

## 📱 스크린샷

| 로그인 | 홈 대시보드 | 보유 코인 목록 | 코인 상세 | 설정 |
|:---:|:---:|:---:|:---:|:---:|
| Google 로그인 | 전체 자산 요약 | LazyColumn 기반 목록 | 거래소별 포지션 | 거래소 연동 관리 |

---

## ✨ 주요 기능

### 1. 로그인
- **Google 소셜 로그인** (Firebase Authentication + Credential Manager API)
- 로그인 성공 후 거래소 미연동 상태이면 설정 페이지로 자동 이동

### 2. 거래소 연동 (설정 페이지)
- 업비트(필수) + 해외 거래소(Gate.io) 연동
- API Key / Secret 입력 → 백엔드 서버를 통한 실시간 키 검증 → Firebase에 안전하게 저장
- 거래소 API 키는 **기기 로컬**에 저장하지 않고 백엔드 서버에서 관리

### 3. 홈 대시보드
- **전체 자산 요약**: 총 평가금액, 총 수익률, 변동금액
- **거래소별 요약**: 도넛 차트로 시각화 (0원인 거래소 자동 제외)
- **Top 5 보유코인**: 평가금액 상위 5개 코인 표시
- **자동 갱신**: 10초 주기로 자산 데이터 자동 새로고침

### 4. 보유 코인 리스트 (Holdings)
- 코인별 심볼, 수량, 평가금액, 수익률 표시
- 정렬 (평가금액/수익률), 검색 기능
- `LazyColumn`으로 성능 최적화

### 5. 코인 상세 (CoinDetail)
- 특정 코인의 거래소별 합산 포지션
- 거래소별 보유량·평균단가·평가금액 확인

### 6. 설정
- 거래소 API 키 연동/수정/삭제

---

## 🏗️ 아키텍처

```
Clean Architecture + MVVM
├── presentation/   # UI Layer (Compose, ViewModel, UiState)
├── domain/         # Business Logic (UseCase, Repository Interface, Model)
└── data/           # Data Layer (API, DTO, Interceptor, Repository Implementation)
```

### 주요 설계 패턴
- **Factory Pattern**: 거래소별 Calculator 분기 (`BalanceCalculatorFactory`)
- **Strategy Pattern**: 국내/해외 잔고 계산 로직 분리 (`UpbitBalanceCalculator`, `ForeignBalanceCalculator`)
- **Repository Pattern**: 데이터 소스 추상화
- **Interceptor Pattern**: Firebase 토큰 자동 주입 (`FirebaseAuthInterceptor`), 거래소 서명 자동 주입 (`UpbitAuthInterceptor`, `GateIOAuthInterceptor`)

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|:---|:---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose, Material3 |
| **DI** | Dagger Hilt |
| **Network** | Retrofit, OkHttp, Kotlinx Serialization |
| **Auth** | Firebase Authentication (Google 로그인), Credential Manager API |
| **Backend** | Firebase Functions (Cloud Run) — 거래소 키 검증 및 자산 조회 프록시 |
| **Async** | Kotlin Coroutines, Flow |
| **Exchange Auth** | JWT (업비트), HMAC-SHA512 (Gate.io) |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 36 |

---

## 📁 프로젝트 구조

```
app/src/main/java/com/crypto/cryptoview/
├── CryptoViewApplication.kt
├── data/
│   ├── auth/
│   │   ├── FirebaseTokenProvider.kt       # Firebase ID Token 인터페이스
│   │   ├── FirebaseTokenProviderImpl.kt   # Firebase ID Token 비동기 발급
│   │   └── GoogleAuthService.kt           # Google 로그인 서비스
│   ├── local/
│   │   ├── CredentialsManager.kt          # 거래소 API 키 로컬 캐시 관리
│   │   └── CredentialsProvider.kt         # 자격증명 제공자
│   ├── remote/
│   │   ├── api/
│   │   │   ├── AuthApi.kt                 # 백엔드 업비트 키 검증/저장/자산조회 API
│   │   │   ├── UpbitApi.kt                # 업비트 직접 REST API
│   │   │   └── GateIOApi.kt               # Gate.io REST API
│   │   ├── dto/                           # 데이터 전송 객체
│   │   └── interceptor/
│   │       ├── FirebaseAuthInterceptor.kt # 백엔드 요청에 Firebase 토큰 자동 주입
│   │       ├── UpbitAuthInterceptor.kt    # 업비트 JWT 자동 주입
│   │       ├── GateIOAuthInterceptor.kt   # Gate.io HMAC 자동 주입
│   │       └── AccountResponseLoggingInterceptor.kt  # 디버그용 응답 바디 로깅
│   └── repository/                        # Repository 구현체
├── domain/
│   ├── model/                             # 도메인 모델
│   ├── repository/                        # Repository 인터페이스
│   └── usecase/
│       ├── auth/                          # 인증 관련 UseCase
│       ├── upbit/                         # 업비트 UseCase
│       ├── gate/                          # Gate.io UseCase
│       ├── calculator/                    # 잔고 계산 전략
│       └── GetAllHoldingsUseCase.kt       # 통합 보유 자산 조회
├── presentation/
│   ├── login/                             # Google 로그인 화면
│   ├── main/                              # 메인 네비게이션
│   ├── settings/                          # 거래소 연동/설정 화면
│   └── component/
│       ├── assetsOverview/                # 홈 대시보드
│       ├── holdingCoinView/               # 코인 상세
│       └── holdingColins/                 # 보유 코인 목록
├── di/                                    # Hilt 모듈 (NetworkModule, RepositoryModule)
├── ui/                                    # 테마, 컬러
└── util/                                  # 유틸리티
```

---

## 🔐 보안

- **Google 로그인**: Firebase Authentication 기반, Credential Manager API 사용
- **API 키 서버 관리**: 거래소 API 키는 기기에 저장하지 않고 백엔드 서버(Firebase)에서 관리
- **Firebase ID Token**: 백엔드 요청 시 자동으로 Bearer 토큰을 주입하여 인증
- **HTTPS 통신**: 모든 API 요청 암호화
- **자동 매매 미지원**: View 전용 앱 (읽기만 가능)
- **민감 정보 로그 제외**: 프로덕션 빌드에서 로깅 인터셉터 비활성화

---

## 📋 진행 상황

### ✅ 완료

| 기능 | 상태 | 설명 |
|:---|:---:|:---|
| Google 로그인 | ✅ | Firebase Auth + Credential Manager API |
| 업비트 연동 | ✅ | 백엔드 키 검증 및 자산 조회 프록시 |
| Gate.io 연동 | ✅ | 현물 잔고/시세 조회, HMAC 인증 |
| USDT→KRW 환산 | ✅ | 업비트 USDT/KRW 시세 기준 자동 환산 |
| 홈 대시보드 | ✅ | 전체 자산 요약, 도넛 차트, Top 5 Holdings |
| 보유 코인 목록 | ✅ | LazyColumn, 정렬, 검색, 0원 필터링 |
| 코인 상세 | ✅ | 거래소별 포지션 확인 |
| 설정 페이지 | ✅ | 거래소 연동 관리 UI |
| Firebase 인터셉터 | ✅ | 백엔드 요청 시 ID Token 자동 주입 |
| 심볼 정규화 | ✅ | 다중 거래소 동일 코인 통합 |

### 🚧 진행 중 / 예정

| 기능 | 상태 | 설명 |
|:---|:---:|:---|
| Gate.io 선물 | 🚧 | 선물 포지션 조회 구현 중 |
| 거래소 미연동 → 강제 이동 | 🚧 | 로그인 후 설정 페이지 자동 리다이렉트 |
| 알림 | 📝 | 목표가 도달, 수익률 알림 |
| 백그라운드 동기화 | 📝 | WorkManager 기반 |
| 거래소 딥링크 | 📝 | 자산 클릭 시 거래소 앱 이동 |

---


## 📄 라이선스

This project is for personal/educational use only.
