# CryptoView

Google 계정으로 로그인한 뒤 여러 거래소의 보유 자산을 한 화면에서 확인하는 Android 앱입니다. 현재는 Upbit와 Gate.io 중심으로 자산 조회, 거래소별 요약, 보유 코인 목록, 코인별 상세 정보를 제공합니다.

Upbit API 키는 앱에 직접 보관하지 않고 백엔드에서 검증 및 저장합니다. 앱은 Firebase ID Token으로 백엔드에 인증 요청을 보내며, Upbit 연동 여부는 로컬 marker로만 관리합니다.

---

## 주요 기능

### Google 로그인

- Firebase Authentication 기반 Google 로그인
- Android Credential Manager API 사용
- 로그인 후 거래소 연동 상태에 따라 설정 화면 또는 메인 화면으로 이동
- 로그아웃 시 Google/Firebase 세션 종료, 로컬 캐시 초기화, 백엔드 Upbit 키 삭제 시도

### 거래소 연동

- Upbit: 백엔드 프록시를 통해 API Key 검증, 저장, 자산 조회, 키 삭제 처리
- Gate.io: Spot/Futures API 모델과 Repository 구성, 로컬 credential 기반 인증 흐름 보유
- Binance, Bybit: credential 모델 필드는 있으나 실제 연동은 TODO

### 자산 개요

- 전체 평가금액, 총 손익, 손익률 표시
- 거래소별 자산 비중 Donut Chart 표시
- 평가금액 기준 Top 5 보유 코인 표시
- 15초 주기 자동 갱신
- 중복 로딩 방지를 위한 `Mutex` 적용

### 보유 코인

- 전체 보유 코인 목록 표시
- 코인명/심볼 검색
- 평가금액, 수익률, 심볼 기준 정렬
- Holdings 탭 진입 시점부터 자동 갱신 시작
- 화면 이탈 시 자동 갱신 중지

### 코인 상세

- 특정 코인의 거래소별 보유 수량, 평균 매수가, 현재가, 평가금액, 손익 표시
- KRW/USDT 기준 가격 단위 구분
- 여러 거래소에 같은 코인을 보유한 경우 통합 및 거래소별 상세 확인

### 테마 및 설정

- 다크/라이트/시스템 테마 지원
- 상태바 아이콘 색상 동적 조정
- 거래소 연동/해제
- 로그아웃

---

## 아키텍처

```text
Clean Architecture + MVVM

presentation/   UI Layer: Jetpack Compose, ViewModel, UiState
domain/         Domain Layer: UseCase, Repository Interface, Model
data/           Data Layer: API, DTO, Interceptor, Repository Implementation, Local Storage
di/             Hilt Module
ui/             Theme, Color, Type
util/           Utility, exchange auth helper
```

### 주요 패턴

- Repository Pattern: 데이터 접근 로직을 interface와 implementation으로 분리
- Strategy Pattern: 거래소별 잔고 계산 로직 분리
- Factory Pattern: 거래소 타입에 맞는 잔고 계산기 선택
- OkHttp Interceptor: Firebase 토큰, Upbit JWT, Gate.io HMAC 인증 주입

---

## 기술 스택

| 분류 | 기술 |
|:---|:---|
| Language | Kotlin 2.3.0 |
| Android | AGP 8.13.2, minSdk 23, targetSdk 36 |
| UI | Jetpack Compose, Material3 |
| DI | Dagger Hilt |
| Network | Retrofit 3, OkHttp 5, Kotlinx Serialization |
| Auth | Firebase Authentication, Credential Manager API |
| Backend | Cloud Run 기반 백엔드 API |
| Database | Firestore, Google KMS 기반 키 관리 |
| Async | Kotlin Coroutines, Flow |
| Local Storage | DataStore Preferences |
| Test | JUnit, MockK, AndroidX Test |

---

## 프로젝트 구조

```text
app/src/main/java/com/crypto/cryptoview/
├── CryptoViewApplication.kt
├── data/
│   ├── auth/
│   │   ├── FirebaseTokenProvider.kt
│   │   ├── FirebaseTokenProviderImpl.kt
│   │   └── GoogleAuthService.kt
│   ├── local/
│   │   ├── CredentialsManager.kt
│   │   ├── CredentialsProvider.kt
│   │   ├── SecureStorage.kt
│   │   └── ThemeManager.kt
│   ├── remote/
│   │   ├── api/
│   │   ├── dto/
│   │   └── interceptor/
│   └── repository/
├── domain/
│   ├── model/
│   │   ├── asset/
│   │   ├── auth/
│   │   ├── exchange/
│   │   ├── gate/
│   │   ├── settings/
│   │   └── upbit/
│   ├── repository/
│   ├── usecase/
│   │   ├── auth/
│   │   ├── calculator/
│   │   ├── gate/
│   │   └── upbit/
│   └── util/
├── presentation/
│   ├── component/
│   │   ├── assetsOverview/
│   │   └── holdingCoinView/
│   ├── login/
│   ├── main/
│   └── settings/
├── di/
├── ui/
└── util/
```

### 도메인 모델 분리 기준

| 패키지 | 역할 |
|:---|:---|
| `domain.model.asset` | 보유 자산, 통합 보유, 거래소별 평가, 상세 자산 모델 |
| `domain.model.upbit` | Upbit 잔고 및 티커 모델 |
| `domain.model.gate` | Gate.io Spot/Futures 잔고 및 티커 모델 |
| `domain.model.exchange` | 거래소 타입, 거래소 credential 모델 |
| `domain.model.auth` | 인증 사용자 모델 |
| `domain.model.settings` | 앱 설정 모델 |

---

## 백엔드 연동

앱은 Firebase ID Token을 `FirebaseAuthInterceptor`에서 Bearer 토큰으로 주입해 백엔드 API를 호출합니다.

현재 백엔드 Base URL:

```text
https://cryptoview-api-xt7sre5ska-du.a.run.app/
```

주요 백엔드 API:

- `POST /api/exchange/upbit/validate-and-save`: Upbit API Key 검증 및 저장
- `GET /api/exchange/upbit/accounts`: Upbit 계정 잔고 조회
- `DELETE /api/exchange/upbit/credential`: 저장된 Upbit credential 삭제

---

## 보안 정책

- Google 로그인은 Firebase Authentication과 Credential Manager API를 사용합니다.
- 백엔드 요청에는 Firebase ID Token을 Bearer 토큰으로 주입합니다.
- Upbit API Key/Secret은 앱 로컬에 저장하지 않고 백엔드에서 Google KMS 기반으로 관리합니다.
- 앱 로컬에는 Upbit 연동 여부 marker만 저장합니다.
- Gate.io 등 기타 거래소는 현재 로컬 credential 저장 코드가 남아 있어 추후 백엔드 저장 방식으로 통합할 필요가 있습니다.
- 모든 네트워크 요청은 HTTPS 기반으로 수행합니다.
- 출금 권한이 없는 Read Only API Key 사용을 전제로 합니다.

---

## 자산 계산 흐름

```text
1. 거래소별 잔고 조회
2. 보유 심볼 기준 현재가 조회
3. Upbit USDT/KRW 시세를 기준으로 환율 산정
4. 거래소별 평가금액 계산
5. 동일 심볼 보유 자산 통합
6. 전체 평가금액, 손익, 거래소별 비중 계산
```

계산 관련 주요 클래스:

- `GetAllHoldingsUseCase`
- `CalculateBalanceUseCase`
- `BalanceCalculatorFactory`
- `UpbitBalanceCalculator`
- `ForeignBalanceCalculator`
- `ExchangeRateProvider`
- `HoldingAggregator`

---

## 실행 방법

### 1. 저장소 클론

```bash
git clone <repository-url>
cd CryptoView
```

### 2. Firebase 설정

`app/google-services.json` 파일이 필요합니다. Firebase 프로젝트 설정에서 Android 앱용 `google-services.json`을 내려받아 `app/` 디렉터리에 배치합니다.

### 3. 빌드

```bash
./gradlew build
```

Windows 환경:

```powershell
.\gradlew.bat build
```

### 4. 앱 실행

Android Studio에서 앱을 실행한 뒤 다음 흐름으로 사용합니다.

```text
Google 로그인 → Settings에서 거래소 연동 → Home에서 자산 확인
```

---

## 테스트

대표 테스트 파일:

- `UpbitAccountBalanceDtoTest`
- `BackendApiIntegrationTest`
- `UpbitRepositoryIntegrationTest`
- `UpbitRepositoryImplTest`

Kotlin 컴파일 확인:

```powershell
.\gradlew.bat :app:compileDebugKotlin
```

현재 알려진 Gradle 경고:

```text
ksp-2.2.10-2.0.2 is too old for kotlin-2.3.0
```

KSP 버전을 Kotlin 2.3.0에 맞춰 올리거나 Kotlin 버전을 KSP에 맞춰 낮추는 정리가 필요합니다.

---

## 현재 완료된 항목

- Google 로그인
- Google/Firebase 로그아웃 안정화
- Upbit 백엔드 기반 credential 검증/저장/삭제
- Upbit 백엔드 프록시 기반 잔고 조회
- Gate.io Spot/Futures API 구조
- 전체 자산 개요 화면
- 거래소별 자산 비중 차트
- Top 5 보유 코인
- 보유 코인 목록, 검색, 정렬
- 코인 상세 화면
- USDT/KRW 환산
- 다크/라이트/시스템 테마
- 도메인 모델 패키지 역할별 분리
- 첫 로그인 후 초기 로딩 중복 호출 완화

---

## TODO

- Gate.io credential도 백엔드 저장 방식으로 통합
- Binance, Bybit, OKX 연동
- 자산 조회 API 병렬화
- Firebase ID Token 캐싱 최적화
- 백엔드에 가벼운 거래소 연동 상태 확인 API 추가
- 첫 로그인 후 `AssetsOverview` 초기 렌더링 추가 최적화
- WorkManager 기반 백그라운드 동기화
- 알림 기능
- 가격/수익률 차트 추가
- README_DETAIL.md 한글 인코딩 및 내용 정리
- 코드 주석 한글 인코딩 정리

---

## 라이선스

This project is licensed under the [MIT License](LICENSE).
