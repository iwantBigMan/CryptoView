# CryptoView Android

CryptoView는 Google 계정으로 로그인한 뒤 Upbit와 Gate.io의 보유 자산을 한 화면에서 확인하고, 현재 포트폴리오 상태를 AI로 요약할 수 있는 Android 앱입니다.

거래소 API Key/Secret은 Android 기기에 저장하지 않습니다. 앱은 Firebase ID Token으로 백엔드에 인증 요청을 보내고, 거래소 credential 검증/저장/삭제와 private asset 조회는 Cloud Run 백엔드에서 처리합니다.

---

## 주요 기능

### Google 로그인

- Firebase Authentication 기반 Google 로그인
- Android Credential Manager API 사용
- 로그인 상태와 거래소 연동 상태에 따라 초기 화면 분기
- 로그아웃 시 Firebase/Google 세션 정리, 로컬 연동 marker 초기화, 백엔드 credential 삭제 요청

### 거래소 연동

- Upbit
  - 백엔드 API를 통한 API Key 검증/저장
  - 백엔드 API를 통한 계정 자산 조회
  - 백엔드 API를 통한 credential 삭제
  - 기기에는 API Key/Secret을 저장하지 않고 `UPBIT_LINKED` marker만 저장
- Gate.io
  - 백엔드 API를 통한 API Key 검증/저장
  - 백엔드 API를 통한 Spot 계정 자산 조회
  - 백엔드 API를 통한 credential 삭제
  - Gate.io Spot 평균단가는 백엔드 `spot-average-price` API 사용
  - Spot ticker는 Gate.io 공개 API 사용
  - 기기에는 API Key/Secret을 저장하지 않고 `GATEIO_LINKED` marker만 저장

### 자산 Overview

- 전체 평가금액, 총 손익, 수익률 표시
- 거래소별 자산 비중 Donut Chart 표시
- 평가금액 기준 Top 5 보유 코인 표시
- 전체 보유 자산 기준 총 손익 계산
- 15초 주기 자동 갱신
- 중복 로딩 방지를 위한 `Mutex` 적용

### 보유 코인

- 전체 보유 코인 목록 표시
- 코인명/심볼 검색
- 평가금액, 손익률 기준 정렬
- KRW/USDT 표시 통화 전환 반영

### 코인 상세

- 코인별 거래소 보유 수량, 평균단가, 현재가, 평가금액, 손익 표시
- Gate.io 평균단가는 백엔드 평균단가 API로 보강
- 한화 표시 상태에서는 Gate.io 평균단가/현재가/평가금액도 KRW 기준으로 표시
- 평균단가가 없는 항목은 손익을 임의 계산하지 않고 정보 없음 상태로 처리

### AI 포트폴리오 요약

- Overview 화면에서 AI 포트폴리오 요약 버튼 제공
- 버튼 클릭 시 전체 자산을 최신화한 뒤 AI 분석용 포트폴리오 스냅샷 생성
- AI 요청에는 화면 표시용 자산 데이터만 포함
- 거래소 API Key/Secret은 AI 요청 body에 포함하지 않음
- 동일 스냅샷 기준 5분 메모리 캐시 적용
- AI 분석 결과는 앱 컨셉에 맞는 다이얼로그로 표시
- AI 분석은 투자 추천이 아니라 포트폴리오 상태 요약/주의점 안내로 취급

### 설정

- 다크/라이트 테마 전환
- KRW/USDT 표시 통화 전환
- 거래소 연동/해제
- 로그아웃

---

## 아키텍처

```text
Clean Architecture + MVVM

presentation/   UI Layer: Jetpack Compose, ViewModel, UiState
domain/         Domain Layer: UseCase, Repository Interface, Domain Model, Mapper
data/           Data Layer: API, DTO, Mapper, Interceptor, Repository Implementation, Local Storage
di/             Hilt Module
ui/             Theme, Color
```

### 레이어 규칙

- `presentation`은 화면 상태와 사용자 이벤트를 다룹니다.
- `domain`은 UseCase, Repository interface, Domain model을 포함합니다.
- `data`는 Retrofit API, DTO, mapper, repository 구현체, local storage를 포함합니다.
- UI에서 Retrofit을 직접 호출하지 않습니다.
- ViewModel에 과도한 데이터 변환 로직을 넣지 않고 Mapper/UseCase로 분리합니다.
- 거래소 credential은 로컬 저장이 아니라 백엔드 저장을 기본 정책으로 합니다.

---

## 기술 스택

| 분류 | 기술 |
|:---|:---|
| Language | Kotlin 2.3.0 |
| Android | AGP 8.13.2, minSdk 23, targetSdk 36 |
| UI | Jetpack Compose, Material3 |
| Architecture | MVVM, Clean Architecture |
| DI | Dagger Hilt |
| Network | Retrofit 3, OkHttp 5, Kotlinx Serialization |
| Auth | Firebase Authentication, Credential Manager API |
| Backend | Cloud Run, Firebase ID Token 인증 |
| Local Storage | DataStore Preferences |
| Async | Kotlin Coroutines, Flow |
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
│   ├── mapper/
│   ├── model/
│   │   ├── ai/
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
| `data.auth` | Firebase token provider |
| `data.local` | DataStore, 테마, 표시 통화, 거래소 연동 marker 관리 |
| `data.remote.api` | 백엔드/거래소 공개 API Retrofit interface |
| `data.remote.dto` | 네트워크 요청/응답 DTO |
| `data.remote.interceptor` | Firebase 인증 토큰 주입, 응답 로깅 |
| `data.remote.mapper` | DTO와 domain model 변환 |
| `data.repository` | Repository 구현체 |
| `domain.model.ai` | AI 포트폴리오 스냅샷/인사이트 모델 |
| `domain.model.asset` | 보유 자산/평가금액/손익 모델 |
| `domain.repository` | Repository interface |
| `domain.usecase` | 기능별 비즈니스 흐름 |
| `presentation` | Compose 화면, ViewModel, UiState |

---

## 백엔드 URL 정책

거래소 Private API와 AI 분석 API는 서로 다른 Retrofit Base URL을 사용합니다.

### BuildConfig

| 값 | 용도 | Debug | Release |
|:---|:---|:---|:---|
| `EXCHANGE_BACKEND_BASE_URL` | 거래소 private API | Cloud Run | Cloud Run |
| `AI_BACKEND_BASE_URL` | AI 분석 API | 로컬 백엔드 | Cloud Run |
| `GATE_BASE_URL` | Gate.io 공개 API | Gate.io 공식 API | Gate.io 공식 API |

기본 URL:

```text
Cloud Run:
https://cryptoview-api-620339426938.asia-northeast3.run.app/

Debug AI local:
http://10.0.2.2:8080/

Gate.io public:
https://api.gateio.ws/api/v4/
```

실기기에서 로컬 AI 백엔드를 테스트할 때는 `local.properties`에 아래 값을 추가합니다.

```properties
debug.ai.backend.base.url=http://192.168.0.10:8080/
```

### Retrofit 분리

- `@ExchangeRetrofit`
  - `EXCHANGE_BACKEND_BASE_URL` 사용
  - Upbit accounts
  - Gate.io accounts
  - Gate.io average price
  - credential validate/save/delete
- `@AiRetrofit`
  - `AI_BACKEND_BASE_URL` 사용
  - AI portfolio insight API

이 정책 때문에 Debug 빌드에서도 Upbit/Gate.io private API는 로컬 백엔드로 가지 않고 Cloud Run을 사용합니다. 거래소 whitelist에 등록된 Cloud Run 고정 IP를 유지하기 위한 구조입니다.

---

## 주요 백엔드 API

모든 백엔드 요청에는 `FirebaseAuthInterceptor`가 Firebase ID Token을 `Authorization: Bearer <token>` 형태로 주입합니다.

### 거래소 API

```text
POST   api/exchange/upbit/validate-and-save
GET    api/exchange/upbit/accounts
DELETE api/exchange/upbit/credential

POST   api/exchange/gateio/validate-and-save
GET    api/exchange/gateio/accounts
DELETE api/exchange/gateio/credential
POST   api/exchange/gateio/spot-average-price
```

### AI API

```text
POST api/ai/portfolio-insight
```

AI 요청 body는 다음 포트폴리오 스냅샷 구조를 사용합니다.

```json
{
  "baseCurrency": "KRW",
  "totalValuationKrw": 0,
  "totalPnlKrw": 0,
  "totalPnlRate": 0,
  "holdings": [
    {
      "exchange": "Upbit",
      "symbol": "BTC",
      "quantity": 0,
      "valuationKrw": 0,
      "averagePrice": 0,
      "currentPrice": 0,
      "pnlKrw": 0,
      "pnlRate": 0
    }
  ]
}
```

---

## Debug HTTP 설정

Debug AI API는 로컬 HTTP 백엔드를 사용할 수 있으므로 debug sourceSet에만 cleartext 허용 설정을 둡니다.

```text
app/src/debug/AndroidManifest.xml
app/src/debug/res/xml/network_security_config.xml
```

허용 도메인:

```xml
<domain includeSubdomains="true">10.0.2.2</domain>
<domain includeSubdomains="true">localhost</domain>
```

`main/AndroidManifest.xml` 또는 release 설정에는 `usesCleartextTraffic=true`를 넣지 않습니다.

---

## 자산 계산 흐름

```text
1. 거래소별 잔고 조회
2. 보유 심볼 기준 현재가 조회
3. Upbit KRW-USDT 가격으로 USDT/KRW 환율 계산
4. 거래소별 평가금액 계산
5. 동일 심볼 보유 자산 통합
6. 전체 평가금액, 총 손익, 총 수익률 계산
7. AI 분석 요청 시 전체 자산 기준 snapshot 생성
```

주요 클래스:

- `GetAllHoldingsUseCase`
- `CalculateBalanceUseCase`
- `BalanceCalculator`
- `BalanceCalculatorFactory`
- `HoldingAggregator`
- `ExchangeRateProvider`
- `GenerateAiPortfolioInsightUseCase`
- `AiPortfolioSnapshotMapper`

---

## 로컬 실행

### 1. Firebase 설정

`app/google-services.json` 파일이 필요합니다.

### 2. 로컬 AI 백엔드 테스트

백엔드 프로젝트에서 로컬 서버를 실행합니다.

```bash
npm run dev
```

Android Emulator Debug 빌드에서 AI 요청은 다음 URL로 나갑니다.

```text
http://10.0.2.2:8080/api/ai/portfolio-insight
```

거래소 private API는 Debug에서도 Cloud Run으로 나갑니다.

```text
https://cryptoview-api-620339426938.asia-northeast3.run.app/api/exchange/upbit/accounts
https://cryptoview-api-620339426938.asia-northeast3.run.app/api/exchange/gateio/accounts
```

### 3. 실기기 로컬 AI 테스트

PC와 실기기를 같은 Wi-Fi에 연결한 뒤 `local.properties`에 PC IP를 설정합니다.

```properties
debug.ai.backend.base.url=http://192.168.0.10:8080/
```

Windows 방화벽에서 8080 포트 접근이 막혀 있으면 연결이 실패할 수 있습니다.

---

## 테스트

단위 테스트:

```powershell
.\gradlew.bat testDebugUnitTest
```

Release Kotlin 컴파일 확인:

```powershell
.\gradlew.bat :app:compileReleaseKotlin
```

Debug/Release URL 설정까지 함께 확인:

```powershell
.\gradlew.bat testDebugUnitTest :app:compileReleaseKotlin
```

---

## 보안 정책

- Upbit/Gate.io API Key/Secret은 Android 기기에 저장하지 않습니다.
- 거래소 credential은 백엔드에서 관리하며, Google KMS 기반 암호화를 전제로 합니다.
- Android 앱은 거래소 연동 여부 marker만 저장합니다.
- 백엔드 요청 인증은 Firebase ID Token을 사용합니다.
- 거래소 Private API는 Debug/Release 모두 Cloud Run 백엔드를 사용합니다.
- AI 요청에는 화면 표시용 포트폴리오 스냅샷만 포함합니다.
- AI 응답은 투자 추천이 아니라 포트폴리오 상태 요약으로 취급합니다.
- Release 빌드는 Cloud Run HTTPS 백엔드만 사용합니다.

---

## 현재 완료된 항목

- Google 로그인
- Upbit 백엔드 기반 credential 검증/저장/삭제
- Upbit 백엔드 기반 자산 조회
- Gate.io 백엔드 기반 credential 검증/저장/삭제
- Gate.io 백엔드 기반 Spot 자산 조회
- Gate.io 백엔드 기반 Spot 평균단가 조회
- 거래소 API Key/Secret 로컬 저장 경로 제거
- 전체 자산 Overview
- 거래소별 자산 비중 차트
- Top 5 보유 코인
- 보유 코인 목록/검색/정렬
- 코인 상세 화면
- KRW/USDT 표시 통화 전환
- 다크/라이트 테마
- AI 포트폴리오 분석 요청 파이프라인
- AI 분석 결과 다이얼로그
- 거래소/AI 백엔드 URL 분리
- Debug 전용 AI 로컬 백엔드 연결 설정

---

## TODO

- AI 분석 응답 UX 고도화
- AI 분석 실패/빈 포트폴리오 케이스 테스트 강화
- Gate.io 평균단가 보강 범위 확대
- Binance, Bybit, OKX 연동
- 자산 조회 병렬화 최적화
- Firebase ID Token 캐싱 최적화
- WorkManager 기반 백그라운드 동기화
- 가격/손익률 차트 추가
- 알림 기능

---

## License

This project is licensed under the [MIT License](LICENSE).
