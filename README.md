# CryptoView

> 🪙 **멀티 거래소 암호화폐 자산 통합 관리 앱**

Google 계정으로 로그인 후, 여러 거래소(Upbit, Gate.io 등)의 API를 연동해 개인 보유 자산을 한눈에 확인할 수 있는 안드로이드 앱입니다.  
거래소별 자산 요약, 상위 보유 코인, 코인 상세 정보를 확인하고, USDT ↔ KRW 자동 환산 기능을 지원할 예정이고 현재 USDT->KRW 환산만 지원합니다.

---



## ✨ 주요 기능

### 1. 로그인
- **Google 소셜 로그인** (Firebase Authentication + Credential Manager API)
- 로그인 성공 후 거래소 미연동 상태이면 설정 페이지로 자동 이동

### 2. 거래소 연동 (설정 페이지)
- 업비트(필수) + 해외 거래소(Gate.io) 연동
- API Key / Secret 입력 → 백엔드 서버를 통한 실시간 키 검증 → Firestore에 안전하게 저장
- 거래소 API 키는 **기기 로컬**에 저장하지 않고 백엔드 서버에서 관리 (Google KMS 암호화)

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
- 거래소 API 키 연동/삭제
- 다크모드/라이트모드 전환
- 로그아웃 (Google 세션 완전 종료 + 백엔드 키 삭제 + 앱 프로세스 종료)

---

## 🏗️ 아키텍처

```
Clean Architecture + MVVM
├── presentation/   # UI Layer (Compose, ViewModel, UiState)
├── domain/         # Business Logic (UseCase, Repository Interface, Model)
└── data/           # Data Layer (API, DTO, Interceptor, Repository Implementation)
```

### 주요 설계 패턴

- **Factory Pattern**: 거래소별 잔고 계산 전략을 선택하는 객체 생성/분기 구조 적용  
  (`BalanceCalculatorFactory`)

- **Strategy Pattern**: 국내/해외 거래소별 잔고 계산 로직을 분리하여 교체 가능한 구조로 설계  
  (`UpbitBalanceCalculator`, `ForeignBalanceCalculator`)

- **Repository Pattern**: Remote API, Local Storage 등 데이터 소스를 추상화하여 데이터 접근 로직 분리

- **OkHttp Interceptor 활용**: Firebase 인증 토큰 및 거래소 API 서명 로직을 요청 단계에서 자동 주입  
  (`FirebaseAuthInterceptor`, `UpbitAuthInterceptor`, `GateIOAuthInterceptor`)

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|:---|:---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose, Material3 |
| **DI** | Dagger Hilt |
| **Network** | Retrofit, OkHttp, Kotlinx Serialization |
| **Auth** | Firebase Authentication (Google 로그인), Credential Manager API |
| **Backend** | Cloud Run — Node.js / Express / TypeScript 기반 거래소 키 검증 및 자산 조회 프록시 |
| **Database** | Firestore — 거래소 API 키 저장 (Google KMS 암호화) |
| **Async** | Kotlin Coroutines, Flow |
| **Exchange Auth** | JWT (업비트), HMAC-SHA512 (Gate.io) |
| **Local Storage** | DataStore Preferences |
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
│   │   └── GoogleAuthService.kt           # Google 로그인/로그아웃 서비스
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
- **API 키 서버 관리**: 거래소 API 키는 기기에 저장하지 않고 Firestore에서 관리 (Google KMS 암호화)
- **Firebase ID Token**: 백엔드 요청 시 자동으로 Bearer 토큰을 주입하여 인증
- **HTTPS 통신**: 모든 API 요청 암호화
- **자동 매매 미지원**: 현재 View 전용 앱 (긴급 매도 기능 추가 예정)
- **민감 정보 로그 제외**: 프로덕션 빌드에서 로깅 인터셉터 비활성화

---

## 📋 진행 상황

### ✅ 완료

| 기능 | 상태 | 설명 |
|:---|:---:|:---|
| Google 로그인 | ✅ | Firebase Auth + Credential Manager API |
| Google 로그아웃 | ✅ | Firebase 세션 종료 + Credential Manager 자격증명 삭제 + Google GMS 토큰 캐시 초기화 |
| 거래소 미연동 → 강제 이동 | ✅ | 로그인/앱 시작 시 업비트 미연동이면 설정 탭 + 연동 다이얼로그 강제 표시 |
| 업비트 연동 | ✅ | 백엔드 키 검증 및 자산 조회 프록시 |
| Gate.io 연동 | ✅ | 현물 잔고/시세 조회, HMAC 인증 |
| USDT→KRW 환산 | ✅ | 업비트 USDT/KRW 시세 기준 자동 환산 |
| 홈 대시보드 | ✅ | 전체 자산 요약, 도넛 차트, Top 5 Holdings |
| 보유 코인 목록 | ✅ | LazyColumn, 정렬, 검색, 0원 필터링 |
| 코인 상세 | ✅ | 거래소별 포지션 확인 |
| 설정 페이지 | ✅ | 거래소 연동 관리 UI |
| Firebase 인터셉터 | ✅ | 백엔드 요청 시 ID Token 자동 주입 |
| 심볼 정규화 | ✅ | 다중 거래소 동일 코인 통합 |
| 다크/라이트 테마 | ✅ | 동적 테마 전환 + 상태바 색상 자동 조정 |
| 클린 아키텍처 로그아웃 | ✅ | ViewModel 결합도 제거, 단일 책임 원칙 준수 |

### 🚧 진행 중 / 예정

| 기능              | 상태 | 설명 |
|:----------------|:---:|:---|
| Gate.io 백엔드 API | 🚧 | 키 검증/자산 조회 프록시 구현 |
| OKX 백엔드 API     | 📝 | 키 검증/자산 조회 프록시 구현 |
| Binance 백엔드 API | 📝 | 키 검증/자산 조회 프록시 구현 |
| Bybit 백엔드 API   | 📝 | 키 검증/자산 조회 프록시 구현 |
| 알림              | 📝 | 목표가 도달, 수익률 알림 |
| 백그라운드 동기화       | 📝 | WorkManager 기반 |
| 거래소 딥링크         | 📝 | 자산 클릭 시 거래소 앱 이동 |

---

## 🎯 최신 개선사항

### 로그아웃 기능 리팩토링

**문제 상황**
- 로그아웃 후에도 Firestore 키 삭제는 되지만 Google 계정이 로그아웃되지 않는 문제 발생
- SettingsScreen이 GoogleLoginViewModel을 직접 호출하며 강한 결합도 발생

**해결 방안**

1. `GoogleAuthService.signOut()` 강화
```kotlin
✅ Firebase Authentication 세션 완전 종료
✅ Credential Manager 캐시 제거 (저장된 자격증명 삭제)
✅ Google GMS SharedPreferences 토큰 캐시 초기화 (com.google.android.gms.auth)
✅ 로그아웃 상태 검증 및 강제 재시도
```

2. `ExchangeSettingsViewModel` 결합도 개선
```kotlin
// Before: UI에서 두 ViewModel 직접 호출
viewModel.logout()
googleLoginViewModel.signOutSuspend()  // ❌ 강한 결합도

// After: ViewModel이 모든 로그아웃 책임
viewModel.logout()  // ✅ 한 줄로 통합
```

3. `GoogleAuthRepository` 직접 주입으로 `SignOutGoogleUseCase` 제거
```kotlin
// Before
private val signOutGoogle: SignOutGoogleUseCase

// After
private val googleAuthRepository: GoogleAuthRepository
```

**이점**
- 🎯 관심사 분리: 로그아웃 로직이 ViewModel에 집중
- 🏗️ 낮은 결합도: UI는 `viewModel.logout()` 한 줄만 호출
- 🧪 테스트 가능성 향상

---

## 📄 라이선스

This project is licensed under the [MIT License](LICENSE).
