# CryptoView

CryptoView는 Google 계정으로 로그인한 뒤 여러 거래소의 보유 자산을 한 화면에서 확인하는 Android 앱입니다. 현재는 Upbit와 Gate.io를 중심으로 자산 조회, 거래소별 요약, 보유 코인 목록, 코인별 상세 정보를 제공합니다.

Upbit와 Gate.io API Key/Secret은 앱 기기에 저장하지 않습니다. 앱은 Firebase ID Token으로 백엔드에 인증 요청을 보내고, 거래소 키 검증, 저장, 삭제, 일부 자산 조회는 백엔드에서 처리합니다. 기기에는 거래소 연동 완료 여부를 나타내는 marker만 저장합니다.

---

## 주요 기능

### Google 로그인

- Firebase Authentication 기반 Google 로그인
- Android Credential Manager API 사용
- 로그인 상태와 거래소 연동 상태에 따라 로그인, 거래소 설정, 메인 화면으로 이동
- 로그아웃 시 Google/Firebase 세션 종료, 로컬 캐시 초기화, 백엔드 credential 삭제 시도

### 거래소 연동

- Upbit
  - 백엔드 API를 통한 API Key 검증 및 저장
  - 백엔드 API를 통한 보유 자산 조회
  - 백엔드 API를 통한 credential 삭제
  - 기기에는 API Key/Secret을 저장하지 않고 `UPBIT_LINKED` marker만 저장
- Gate.io
  - 백엔드 API를 통한 API Key 검증 및 저장
  - 백엔드 API를 통한 Spot 보유 자산 조회
  - 백엔드 API를 통한 credential 삭제
  - 기기에는 API Key/Secret을 저장하지 않고 `GATEIO_LINKED` marker만 저장
  - Spot ticker 조회는 Gate.io 공식 공개 API 사용
- Binance, Bybit
  - credential 모델 필드는 존재하지만 실제 연동은 TODO

### 자산 개요

- 전체 평가 금액, 총 수익, 수익률 표시
- 거래소별 자산 비중 Donut Chart 표시
- 평가 금액 기준 Top 5 보유 코인 표시
- 15초 주기 자동 갱신
- 중복 로딩 방지를 위한 `Mutex` 적용

### 보유 코인

- 전체 보유 코인 목록 표시
- 코인명 및 심볼 검색
- 평가 금액, 수익률, 심볼 기준 정렬
- Holdings 화면 진입 시 자동 갱신 시작
- 화면 이탈 시 자동 갱신 중지

### 코인 상세

- 특정 코인의 거래소별 보유 수량, 평균 매수가, 현재가, 평가 금액, 수익 표시
- KRW/USDT 가격 단위 구분
- 여러 거래소에 같은 코인을 보유한 경우 통합 및 거래소별 상세 확인

### 테마 및 설정

- 다크/라이트/시스템 테마 지원
- 상태바 및 아이콘 색상 동적 조정
- 거래소 연동 및 해제
- 로그아웃

---

## 아키텍처

```text
Clean Architecture + MVVM

presentation/   UI Layer: Jetpack Compose, ViewModel, UiState
domain/         Domain Layer: UseCase, Repository Interface, Domain Model
data/           Data Layer: API, DTO, Mapper, Interceptor, Repository Implementation, Local Storage
di/             Hilt Module
ui/             Theme, Color, Type
```

### 레이어 규칙

- `domain`은 Android UI, Retrofit DTO, Firebase 구현체에 직접 의존하지 않는 것을 목표로 합니다.
- `presentation`은 ViewModel과 화면 상태를 담당하고, 데이터 접근은 use case 또는 domain repository interface를 통해 수행합니다.
- `data`는 Retrofit API, DTO, mapper, local storage, repository 구현체를 담당합니다.
- 거래소별 credential 저장 방식은 앱 로컬 저장이 아니라 백엔드 저장 방식을 기본으로 합니다.

### 주요 패턴

- Repository Pattern: 데이터 접근 로직을 interface와 implementation으로 분리
- UseCase Pattern: 화면 요구사항과 도메인 흐름을 명시적으로 분리
- Strategy Pattern: 거래소별 잔고 계산 로직 분리
- Factory Pattern: 거래소 타입에 맞는 잔고 계산기 선택
- OkHttp Interceptor: Firebase ID Token을 백엔드 요청에 Bearer 토큰으로 주입

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
│   ├── local/
│   ├── remote/
│   │   ├── api/
│   │   ├── dto/
│   │   ├── interceptor/
│   │   └── mapper/
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
│   └── util/
├── di/
├── presentation/
└── ui/
```

### 주요 패키지

| 패키지 | 역할 |
|:---|:---|
| `data.auth` | Google/Firebase 인증 처리 |
| `data.local` | DataStore, 테마, 로컬 연동 marker 관리 |
| `data.remote.api` | 백엔드 및 거래소 공개 API Retrofit interface |
| `data.remote.dto` | 네트워크 응답 DTO |
| `data.remote.interceptor` | Firebase 인증 토큰 주입 및 응답 로깅 |
| `data.remote.mapper` | DTO와 domain model 변환, 백엔드 오류 메시지 매핑 |
| `data.repository` | repository 구현체 |
| `domain.model.asset` | 통합 보유 자산 및 계산용 모델 |
| `domain.model.auth` | credential 검증 및 삭제 결과 모델 |
| `domain.model.exchange` | 거래소 타입 및 credential 모델 |
| `domain.model.gate` | Gate.io Spot/Futures 잔고 및 ticker 모델 |
| `domain.model.upbit` | Upbit 잔고 및 ticker 모델 |
| `domain.usecase` | 화면과 기능별 도메인 흐름 |
| `presentation` | Compose 화면, ViewModel, UiState |

---

## 백엔드 연동

앱은 Firebase ID Token을 `FirebaseAuthInterceptor`에서 Bearer 토큰으로 주입해 백엔드 API를 호출합니다.

현재 백엔드 Base URL:

```text
https://cryptoview-api-620339426938.asia-northeast3.run.app/
```

주요 백엔드 API:

- `POST /api/exchange/upbit/validate-and-save`: Upbit API Key 검증 및 저장
- `GET /api/exchange/upbit/accounts`: Upbit 계정 잔고 조회
- `DELETE /api/exchange/upbit/credential`: 저장된 Upbit credential 삭제
- `POST /api/exchange/gateio/validate-and-save`: Gate.io API Key 검증 및 저장
- `GET /api/exchange/gateio/accounts`: Gate.io Spot 계정 잔고 조회
- `DELETE /api/exchange/gateio/credential`: 저장된 Gate.io credential 삭제

### Gate.io 백엔드 전환

Gate.io는 Android에서 API Key/Secret을 직접 저장하고 HMAC 인증 헤더를 생성하던 방식에서 백엔드 기반 credential 관리 방식으로 전환했습니다.

- Android 로컬 API Key/Secret 저장 제거
- Gate.io HMAC 인증 interceptor 및 auth helper 제거
- `GATEIO_LINKED` marker만 로컬에 저장
- 키 검증/저장, 계정 조회, 키 삭제는 백엔드 API 사용
- Spot ticker 조회는 인증이 필요 없는 Gate.io 공개 API 사용
- Gate.io 미연동 상태에서는 전체 보유자산 조회 중 Gate.io 계정 API를 호출하지 않음
- 저장/조회/삭제 작업별 오류 메시지를 분리해 404 응답 메시지가 잘못 표시되지 않도록 처리

> 현재 Cloud Run 배포 상태에서 Gate.io 백엔드 엔드포인트가 404를 반환하는 로그를 확인했습니다. Android 연동 코드는 백엔드 엔드포인트가 배포되면 동작하도록 준비되어 있습니다.

---

## 보안 정책

- Google 로그인은 Firebase Authentication과 Credential Manager API를 사용합니다.
- 백엔드 요청에는 Firebase ID Token을 Bearer 토큰으로 주입합니다.
- Upbit와 Gate.io API Key/Secret은 기기에 저장하지 않습니다.
- 거래소 API Key/Secret은 백엔드에서 Google KMS 기반으로 관리하는 것을 전제로 합니다.
- 기기에는 거래소별 연동 여부 marker만 저장합니다.
  - `UPBIT_LINKED`
  - `GATEIO_LINKED`
- 모든 네트워크 요청은 HTTPS 기반으로 수행합니다.
- 거래소 API Key는 출금 권한이 없는 Read Only Key 사용을 전제로 합니다.

---

## 자산 계산 흐름

```text
1. 거래소별 잔고 조회
2. 보유 심볼 기준 현재가 조회
3. Upbit USDT/KRW 시세를 기준으로 환율 산정
4. 거래소별 평가 금액 계산
5. 동일 심볼 보유 자산 통합
6. 전체 평가 금액, 수익, 거래소별 비중 계산
```

주요 클래스:

- `GetAllHoldingsUseCase`
- `CalculateBalanceUseCase`
- `BalanceCalculator`
- `BalanceCalculatorFactory`
- `HoldingAggregator`
- `ExchangeRateProvider`

---

## 로컬 설정

### 1. Firebase 설정

`app/google-services.json`이 필요합니다.

### 2. 백엔드 URL

백엔드 URL은 `app/build.gradle.kts`의 `BuildConfig.BACKEND_BASE_URL`로 관리합니다.

```kotlin
buildConfigField(
    "String",
    "BACKEND_BASE_URL",
    "\"https://cryptoview-api-620339426938.asia-northeast3.run.app/\""
)
```

Gate.io 공개 API URL은 `BuildConfig.GATE_BASE_URL`로 관리합니다.

```kotlin
buildConfigField(
    "String",
    "GATE_BASE_URL",
    "\"https://api.gateio.ws/api/v4/\""
)
```

### 3. 실행

Android Studio에서 앱을 실행한 뒤 다음 흐름으로 사용합니다.

```text
Google 로그인 → Settings에서 거래소 연동 → Home에서 자산 확인
```

---

## 테스트

주요 테스트 파일:

- `UpbitAccountBalanceDtoTest`
- `BackendApiIntegrationTest`
- `UpbitRepositoryIntegrationTest`
- `UpbitRepositoryImplTest`

단위 테스트 실행:

```powershell
.\gradlew.bat testDebugUnitTest
```

Kotlin 컴파일 확인:

```powershell
.\gradlew.bat :app:compileDebugKotlin
```

---

## 현재 완료된 항목

- Google 로그인
- Google/Firebase 로그아웃 안정화
- Upbit 백엔드 기반 credential 검증, 저장, 삭제
- Upbit API Key/Secret 기기 저장 경로 제거
- Upbit 백엔드 프록시 기반 잔고 조회
- Gate.io 백엔드 기반 credential 검증, 저장, 삭제 연동 코드
- Gate.io 백엔드 기반 Spot 잔고 조회 연동 코드
- Gate.io 로컬 API Key/Secret 저장 및 HMAC 인증 코드 제거
- 전체 자산 개요 화면
- 거래소별 자산 비중 차트
- Top 5 보유 코인
- 보유 코인 목록, 검색, 정렬
- 코인 상세 화면
- USDT/KRW 환산
- 다크/라이트/시스템 테마
- 도메인 모델 패키지 역할별 분리
- 일부 클린 아키텍처 의존성 정리
- 첫 로그인 후 초기 로딩 중복 호출 완화

---

## TODO

- Gate.io 백엔드 엔드포인트 배포 및 404 해소
- Gate.io domain DTO 의존성 추가 제거
- Binance, Bybit, OKX 연동
- 자산 조회 API 병렬화
- Firebase ID Token 캐싱 최적화
- 백엔드에 가벼운 거래소 연동 상태 확인 API 추가
- 첫 로그인 후 `AssetsOverview` 초기 로딩 최적화
- WorkManager 기반 백그라운드 동기화
- 알림 기능
- 가격 및 수익률 차트 추가

---

## 라이선스

This project is licensed under the [MIT License](LICENSE).
