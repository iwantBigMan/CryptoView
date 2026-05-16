# 사용한 프롬프트

## 기능 관련 프롬프트
- 작업 기록용 `prompt.txt` 파일을 먼저 생성한다.
- 수정된 `AGENTS.md` 지침을 반영하여 `prompt.txt`를 `prompt.md`로 교체한다.
- 환율을 별도로 계산하고 있으므로 사용하지 않는 `upbitAllTickers` 입력을 제거한다.

## 테스트 관련 프롬프트
- 없음

## 기타 프롬프트
- 앞으로 이 저장소에서는 `AGENTS.md` 지침을 따른다.
- 현재 브랜치의 코드 구조를 어느 정도 파악했는지 확인한다.
- 브랜치의 코드 구조를 먼저 분석한다.
- 선물 포지션 레포지토리는 아직 구현하지 말고, 계산기 쪽 문제만 설명한다.
- 수정된 `AGENTS.md` 지침을 다시 확인하고 현재 작업 방식에 적용한다.
- 로그아웃 후에도 Google 로그인 상태가 유지되는 이유를 확인한다.
- 기기 Google 계정은 유지하되 앱과 계정이 자동으로 다시 연동되지 않도록 조정한다.
- 로그아웃 버튼을 눌러도 앱의 Google 로그인 상태가 남는 문제를 다시 확인하고, 실패가 숨겨지지 않도록 처리한다.
- 로그아웃 완료 후 앱을 다시 실행하면 거래소 키만 삭제되고 Google 로그인 세션은 남아 있는 문제를 수정한다.
- 로그아웃 완료 후 Firebase 로그인 해제를 확인하고 로그인 화면으로 전환한 뒤 `finishAndRemoveTask()`로 앱 태스크를 종료한다.

- 첫 로그인 이후 `AssetsOverview` 렌더링 속도가 느렸는지 코드 흐름 기준으로 확인한다.
- `AssetsOverviewViewModel`, `MainActivity`, `MainScreen`, `GetAllHoldingsUseCase`, 관련 Repository와 Firebase 토큰 인터셉터를 확인해 첫 진입 병목 가능성을 분석한다.

- 첫 로그인 이후 `AssetsOverview` 렌더링 속도 개선 방안 1, 2, 3을 구현한다.
- `HoldingCoinsViewModel` 자동 갱신을 Holdings 화면 진입 시점으로 미루고, `AssetsOverviewViewModel`의 중복 로딩을 방지한다.
- 업비트 키는 백엔드 Google KMS 저장을 유지하고, 앱 로컬에는 키/시크릿 대신 연동 여부 marker만 저장한다.
- `:app:compileDebugKotlin`으로 수정 후 컴파일을 검증한다.

- 도메인 레이어 `model` 패키지에 몰려 있는 모델을 역할별 하위 패키지로 분리한다.
- `asset`, `upbit`, `gate`, `auth`, `settings`, `exchange` 패키지로 모델을 재배치하고 참조 import를 갱신한다.
- 모델 패키지 분리 후 `:app:compileDebugKotlin`으로 컴파일을 검증한다.

## 기능 관련 프롬프트
- 프로젝트 구조와 현재 README를 파악한 뒤 README에 업데이트할 내용을 정리한다.
- 깨진 한글 문서를 정상 UTF-8 한글 README로 재작성한다.
- 최신 코드 기준으로 인증, 백엔드 연동, 보안 정책, 도메인 모델 패키지 구조, 초기 로딩 성능 개선 내용을 README.md에 반영한다.

## 테스트 관련 프롬프트
- 문서 변경 작업이므로 앱 빌드나 단위 테스트는 실행하지 않는다.
- README.md 변경 후 git diff로 수정 범위를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경 파일은 사용자가 요청하지 않았으므로 수정하지 않는다.
- README_DETAIL.md는 이번 요청 범위에서 제외하고 TODO에 정리한다.

## 기능 관련 프롬프트
- 현재 프로젝트가 클린 아키텍처 원칙을 제대로 지키고 있는지 확인한다.
- `data`, `domain`, `presentation`, `di` 패키지 구조와 의존성 방향을 기준으로 위반 여부를 점검한다.
- 도메인 레이어가 DTO, UI 프레임워크, Android/Firebase 구현체에 의존하는지 확인한다.
- 프레젠테이션 레이어가 데이터 레이어 구현체를 직접 참조하는지 확인한다.

## 테스트 관련 프롬프트
- `testDebugUnitTest`를 실행해 현재 코드가 단위 테스트 기준으로 컴파일 및 테스트 통과하는지 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 폴더 변경은 사용자가 요청하지 않았으므로 수정하지 않는다.
- 점검 결과와 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- Gate.io 영역은 백엔드 구현 후 재작업 예정이므로 이번 수정 범위에서 제외한다.
- `AuthRepository`가 data DTO를 반환하지 않도록 도메인 결과 모델과 data mapper를 추가한다.
- `ExchangeSettingsViewModel`이 `data.local` 구현체에 직접 의존하지 않도록 도메인 repository 인터페이스를 추가한다.
- `ExchangeType`에서 Compose `Color` 의존성을 제거하고 presentation 확장 함수로 색상을 이동한다.
- `MainActivity`에서 FirebaseAuth 직접 접근과 테스트용 토큰 로그를 제거한다.

## 테스트 관련 프롬프트
- `testDebugUnitTest`를 실행해 수정 후 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 수정 후 변경 파일 목록과 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- 업비트 키값은 기기에 저장하지 않는 정책이므로 로컬 저장/삭제 로직 잔여 여부를 확인한다.
- 업비트 API Key/Secret 관련 DataStore preference, 도메인 모델 필드, 직접 인증 인터셉터 연결을 제거한다.
- 업비트는 로컬 키가 아니라 `UPBIT_LINKED` 연동 marker만 유지하도록 정리한다.
- 업비트 키 삭제처럼 보이는 로컬 메서드는 연동 marker 삭제 의미가 드러나도록 이름을 변경한다.

## 테스트 관련 프롬프트
- `testDebugUnitTest`를 실행해 수정 후 컴파일과 단위 테스트 통과 여부를 확인한다.
- 업비트 키 관련 문자열 검색으로 로컬 저장 필드와 직접 인증 코드가 남아 있지 않은지 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 수정 후 변경 파일 목록과 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- README를 현재 코드 상태에 맞춰 업데이트한다.
- 깨진 한글 README를 정상 UTF-8 한국어 문서로 정리한다.
- Upbit API Key/Secret을 기기에 저장하지 않고 `UPBIT_LINKED` marker만 저장한다는 정책을 README에 반영한다.
- 최근 클린 아키텍처 의존성 정리, data mapper, presentation model, Gate.io 재작업 예정 범위를 README에 반영한다.

## 테스트 관련 프롬프트
- 문서 변경 작업이므로 빌드나 단위 테스트는 실행하지 않는다.
- `git diff -- README.md`로 README 변경 범위를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 수정 후 변경 파일 목록과 핵심 변경 내용을 한국어로 요약한다.

## 기능 관련 프롬프트
- Upbit와 향후 Gate.io 백엔드 API가 공통으로 사용할 수 있도록 백엔드 Base URL을 BuildConfig로 이동한다.
- `BACKEND_BASE_URL` BuildConfig 필드를 추가하고 백엔드 Retrofit 생성부가 해당 값을 사용하도록 변경한다.

## 테스트 관련 프롬프트
- `testDebugUnitTest`를 실행해 BuildConfig 생성과 Kotlin 컴파일 및 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 수정 중 발견된 `app/build.gradle.kts` 첫 줄 인코딩 문제를 정상화한다.
- 수정 후 변경 파일 목록과 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- Gate.io 백엔드 MVP 엔드포인트에 맞춰 Android 연동을 구현한다.
- 공통 `BACKEND_BASE_URL`을 새 Cloud Run URL로 갱신한다.
- Gate.io 키 검증/저장, 자산 조회, 키 삭제 API를 Retrofit에 추가한다.
- Gate.io 로컬 API Key/Secret 저장과 HMAC 인증 인터셉터를 제거하고 `GATEIO_LINKED` marker만 저장하도록 변경한다.
- 설정 화면에서 Upbit/Gate.io 중 연동할 거래소를 선택해 검증할 수 있도록 처리한다.
- Gate.io Spot 잔고 조회는 백엔드 API를 사용하고, ticker 조회는 기존 공식 공개 API를 유지한다.

## 테스트 관련 프롬프트
- `testDebugUnitTest`를 실행해 Hilt 주입, BuildConfig 생성, Kotlin 컴파일, 단위 테스트 통과 여부를 확인한다.
- Gate.io 로컬 키 저장 및 HMAC 인증 관련 문자열이 남아 있지 않은지 검색한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 수정 후 변경 파일 목록과 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- Gate.io 백엔드 상태 코드별 Android 오류 메시지 처리를 추가한다.
- 400, 401, 403, 404, 500, 502 응답을 사용자가 이해할 수 있는 Gate.io 안내 메시지로 변환한다.

## 테스트 관련 프롬프트
- `testDebugUnitTest`를 다시 실행해 상태 코드 매핑 추가 후 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 수정 후 변경 파일 목록과 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- `saveSelectedCredentials`에서 Gate.io 키 저장 시 `저장된 Gate.io 키가 없습니다` 오류가 발생하는 원인을 확인한다.
- Cloud Run 로그로 `POST /api/exchange/gateio/validate-and-save`와 `GET /api/exchange/gateio/accounts`의 404 응답 여부를 확인한다.
- Gate.io 백엔드 오류 메시지를 저장, 조회, 삭제 작업별로 분리해 저장 API 404가 저장된 키 없음으로 표시되지 않도록 수정한다.
- Gate.io가 로컬 연동 상태가 아닐 때 전체 보유자산 조회에서 Gate.io 계정 조회 API를 호출하지 않도록 수정한다.

## 테스트 관련 프롬프트
- `rg`로 기존 Gate.io 오류 매퍼 참조가 남아 있는지 확인한다.
- `compileDebugKotlin`을 실행해 Kotlin 컴파일과 Hilt 주입 변경이 정상인지 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 수정 후 변경 파일 목록과 테스트 결과를 한국어로 요약한다.

## 기능 관련 프롬프트
- Gate.io를 백엔드 기반 credential 저장/조회 방식으로 교체한 내용을 README에 반영한다.
- README에 Gate.io 로컬 API Key/Secret 저장 제거, HMAC 인증 코드 제거, `GATEIO_LINKED` marker 저장 방식을 명시한다.
- README의 백엔드 Base URL과 Gate.io 백엔드 API 목록을 현재 Android 코드 기준으로 갱신한다.
- 현재 Cloud Run Gate.io 엔드포인트 404 확인 사항과 남은 TODO를 문서화한다.

## 테스트 관련 프롬프트
- `rg`로 README에 과거 Gate.io 로컬 credential/HMAC/TODO 표현이 부정확하게 남아 있는지 확인한다.
- 문서 수정 작업이므로 빌드와 단위 테스트는 실행하지 않는다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인한다.
- `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.
- README 전체를 정상 한국어 문서로 재작성하고 변경 내용을 한국어로 요약한다.

## 기능 관련 프롬프트
- Gate.io 평균단가 데이터가 백엔드에서 내려오고 있을 텐데 Android 앱에서 표시되지 않는 원인을 확인하고 수정했다.
- `GateSpotBalanceDto`에서 `avg_buy_price`, `avgBuyPrice`, `avg_buy_price_usdt`, `avgBuyPriceUsdt`, `average_buy_price`, `averageBuyPrice` 형태의 평균단가 필드를 받을 수 있게 했다.
- Gate.io 평균단가를 DTO, mapper, domain model, `ForeignBalance`, `HoldingData`, 상세 화면 UseCase까지 전달되도록 수정했다.
- 상세 화면에서 Gate.io 현재가와 평균단가는 USDT 단위로 표시하고, 평가금액과 손익 계산은 기존 KRW 기준을 유지하도록 처리했다.

## 테스트 관련 프롬프트
- Gate.io 평균단가 매핑 테스트를 추가해 snake_case와 camelCase 평균단가 응답을 domain model로 변환하는지 확인했다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 Kotlin 컴파일과 단위 테스트 통과 여부를 확인했다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.
- `GetExchangeHoldingDetailsUseCase.kt` 수정 중 깨진 주석 인코딩을 UTF-8 한국어 주석으로 복구했다.

## 기능 관련 프롬프트
- 백엔드에 구현된 `POST /api/exchange/gateio/spot-average-price` API를 Android에 연동했다.
- Android에서 accessKey/secretKey를 보내지 않고 기존 Firebase ID Token Authorization 헤더 주입 구조를 재사용하도록 구현했다.
- Gate.io 현물 평균단가 request/response DTO를 추가하고, 문자열 숫자는 String으로 유지한 뒤 domain model에서 BigDecimal로 변환하도록 했다.
- `GateSpotRepository`, `GateSpotRepositoryImpl`, `GetGateIoSpotAveragePriceUseCase`를 추가/확장해 평균단가 조회 흐름을 구성했다.
- 보유 상세 화면 진입 시 Gate.io 보유 항목이 있으면 `BTC_USDT` 같은 단일 currencyPair로 평균단가를 조회하도록 ViewModel에 연결했다.
- 평균단가 조회 상태를 loading, success, error로 분리하고 400, 401, 404, 502 응답을 화면 메시지로 구분했다.
- 상세 화면에서 Gate.io 평균단가, API 보유수량, 총 매입금액, 수량 차이 경고, warnings를 표시하도록 추가했다.

## 테스트 관련 프롬프트
- Gate.io 평균단가 응답 DTO가 문자열 숫자를 유지하고 BigDecimal 값으로 노출하는지 단위 테스트를 추가했다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 Kotlin 컴파일, Hilt 주입, 단위 테스트 통과 여부를 확인했다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 docs 신규 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- `HoldingDetailScreen.kt`의 깨진 한글 UI 텍스트와 깨진 금액 포맷 문자열을 정상 한국어/정상 포맷으로 복구했다.
- 보유 상세 화면의 빈 상태, 오류, 거래소별 보유 현황, 수량, 평균 단가, 현재가, 평가 금액, 손익, Gate.io 평균단가 안내 문구를 정리했다.
- KRW 금액은 `₩`, USDT 가격은 `USDT` 단위로 표시되도록 포맷 함수를 정리했다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 상세 화면 텍스트 복구 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인했다.

## 기타 프롬프트
- 작업 전 현재 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 docs 신규 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- Gate.io 평단 책임을 Android balance/accounts 응답이 아니라 백엔드 `spot-average-price` API로 통일한다.
- `GateSpotBalanceDto`, `GateMapper`, `GateSpotBalance`, `ForeignBalance`, `ForeignBalanceCalculator`에 남아 있던 Gate.io balance 기반 평균단가 전달 경로를 제거한다.
- 보유 상세 화면에서 Gate.io 평균단가 API 응답을 받은 뒤 해당 거래소 항목의 평균단가, 수량, 평가금액, 손익, 손익률을 백엔드 평균단가 기준으로 갱신한다.
- View에서는 계산하지 않고 ViewModel에서 백엔드 응답을 기준으로 화면 상태를 완성하도록 정리한다.

## 테스트 관련 프롬프트
- `avgBuyPriceUsdt`, `avg_buy_price_usdt`, `avgBuyPriceCamel`, `averageBuyPrice` 검색으로 기존 Gate.io balance 평균단가 경로가 남아 있지 않은지 확인한다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.
- 첫 테스트 실행은 Windows 파일 잠금으로 `R.jar` 삭제에 실패해 Gradle daemon을 중지한 뒤 재실행했다.

## 기능 관련 프롬프트
- Gate.io 평균단가 API 응답에서 `currentQuantity`는 있지만 `averagePrice`와 `totalCost`가 0으로 내려오는 경우 화면에 `0 USDT`로 표시되지 않게 수정한다.
- 평균단가 또는 총 매입금액이 0 이하이면 상세 화면의 Gate.io 평단/손익 계산에 적용하지 않는다.
- 평균단가 계산에 필요한 매수 거래 내역이 없다는 안내 문구를 표시하고, 총 매입금액은 `-`로 표시한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 수정 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 스크린샷에서 평균 단가와 총 매입금액이 `0 USDT`로 표시되는 문제를 확인했다.
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- Gate.io `spot-average-price` API 호출부에 디버깅 로그를 추가한다.
- 요청 로그에는 `currencyPair`와 심볼만 남기고 Firebase ID Token이나 거래소 키 같은 민감 정보는 남기지 않는다.
- 성공 로그에는 평균단가, 총 매입금액, 계산 수량, 현재 보유 수량, 거래 수, 조회 페이지 수, warnings를 남긴다.
- 실패 로그에는 `currencyPair`와 예외 스택을 남긴다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 로그 추가 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- 기존 코드 구조를 벗어나지 않고 설정 화면에서 KRW/USDT 표시 통화를 전환할 수 있는 버튼을 구현한다.
- 테마 설정과 같은 DataStore 패턴으로 `DisplayCurrency`, `DisplayCurrencyManager`, `DisplayCurrencyViewModel`을 추가한다.
- 내부 자산 계산은 기존 KRW 기준을 유지하고, 화면 표시 단계에서만 선택 통화와 `usdtKrwRate`를 이용해 포맷한다.
- 자산 개요, 보유 코인 목록, 보유 상세 화면에 표시 통화 설정을 반영한다.
- `MainUiState`, `HoldingsUiState`, `HoldingDetailUiState`에 `usdtKrwRate`를 보존해 표시 포맷에서 사용할 수 있게 한다.
- 공통 표시 포맷 함수로 KRW 금액과 USDT 환산 금액을 처리한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 표시 통화 전환 구현 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 첫 컴파일에서 `collectAsState()` delegate용 `getValue` import 누락을 확인하고 수정했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- 설정 화면의 KRW/USDT 표시 통화 전환 버튼 텍스트를 가운데 정렬한다.
- 표시 통화 전환 버튼의 세로 크기를 약 5dp 정도 키운다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 버튼 UI 수정 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 현재 Material3 `FilterChip`에 `contentPadding` 파라미터가 없어 `heightIn(min = 37.dp)`로 버튼 높이를 조정했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- 홈 화면의 총 손익이 Top 5 기준인지 전체 보유자산 기준인지 확인한다.
- 총 손익은 Top 5가 아니라 `allHoldings` 전체 기준으로 계산되는 구조임을 확인한다.
- 평단이 없는 Gate.io 항목에서 `avgBuyPrice = 0`이 들어올 때 평가금액 전체가 손익으로 계산되지 않도록 기본 계산 로직을 수정한다.
- 평단이 0 이하이면 `HoldingData.change`와 `changePercent`를 0으로 두고 `avgBuyPrice`는 null로 유지한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 평단 없는 항목의 손익 계산 수정 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- 앱이 한화 표기 상태일 때 Gate.io 평균단가와 현재가도 KRW로 보여주도록 수정한다.
- Gate.io 평균단가는 백엔드 `spot-average-price` 응답의 USDT 값을 Upbit `KRW-USDT` 환율로 환산해 표시한다.
- Gate.io 현재가는 기존 USDT 현재가에 같은 환율을 곱해 KRW로 표시한다.
- 평균단가가 없거나 0이어도 현재가는 KRW로 표시하고, 평단/손익만 정보 없음 상태로 둔다.
- 화면 함수는 백엔드 원본 평균단가를 직접 포맷하지 않고 ViewModel에서 완성한 `ExchangeHoldingDetail` 값을 표시하도록 정리한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 KRW 환산 표시 변경 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 현재 환율은 클라이언트가 Upbit 공개 ticker API의 `KRW-USDT` 값을 직접 받아 사용하는 구조임을 확인했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- Gate.io 평균단가 응답의 `warnings` 문구를 보유 상세 화면에 표시하지 않도록 제거한다.
- 백엔드 warning 데이터는 유지하되 UI에는 노출하지 않는다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 warning 문구 제거 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 스크린샷에서 365일 거래내역 조회 안내 문구가 `warnings` 출력 블록에서 표시되는 것을 확인했다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- Gate.io 평균단가, 현재가, 평가금액을 KRW 기준으로 맞춘 뒤 손익 계산도 같은 기준으로 다시 계산한다.
- Gate.io 평균단가 API 응답을 적용한 뒤 개별 거래소 항목의 손익과 손익률을 갱신한다.
- 갱신된 거래소별 보유 항목 목록을 기준으로 상세 화면의 전체 평가금액, 총 손익, 총 수익률도 다시 계산한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 손익 재계산 변경 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- AI 포트폴리오 분석 1차 구현 목표를 기준으로, 오버뷰 화면에 AI 분석 버튼을 추가하고 버튼 클릭 시 전체 자산을 최신화한 뒤 AI 분석용 포트폴리오 스냅샷을 생성하도록 구현한다.
- 클라이언트는 투자 판단을 하지 않고 현재 자산 상태를 `baseCurrency`, `totalValuationKrw`, `totalPnlKrw`, `totalPnlRate`, `holdings` 구조로 정리해 백엔드 `/api/ai/portfolio-insight` API에 전달한다.
- 기존 Clean Architecture 구조를 유지하며 DTO, Domain Model, Repository, UseCase, ViewModel, Compose UI를 분리한다.
- AI 분석 결과는 장기 저장하지 않고 Repository 메모리 캐시로 5분 동안 동일 스냅샷의 백엔드 재요청만 방지한다.
- ViewModel은 UI 진행 상태만 관리하고, 전체 자산 조회와 스냅샷 생성 및 캐시 확인은 UseCase/Repository 계층에서 처리한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 AI 포트폴리오 분석 파이프라인 추가 후 Kotlin 컴파일, Hilt 바인딩, Compose 컴파일, 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 백엔드 AI API 호출에는 FirebaseAuthInterceptor를 통한 Firebase ID Token만 사용하고, 거래소 accessKey/secretKey는 요청 body에 포함하지 않는다.

## 기능 관련 프롬프트
- AI 분석 버튼 클릭 시 오버뷰 카드 안에 결과를 직접 펼치지 않고 `AiPortfolioInsightDialog`를 띄워 로딩, 성공, 오류 상태를 표시하도록 수정한다.
- 다이얼로그 디자인은 기존 앱 컨셉에 맞춰 `LocalAppColors`의 카드 배경, surfaceVariant, accentBlue를 사용하고, 헤더 아이콘, 스크롤 가능한 분석 본문, 모델 정보, 재시도 버튼을 포함한다.
- 오버뷰의 AI 분석 카드는 버튼 실행 역할만 맡고, 분석 결과 본문은 다이얼로그에서만 보여주도록 UI 책임을 분리한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 AI 분석 다이얼로그 UI 수정 후 Kotlin/Compose 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 테스트 실행 후 생성된 `.kotlin` 빌드 산출물은 작업 변경에 포함되지 않도록 정리한다.

## 기능 관련 프롬프트
- 로컬 개발용 백엔드 Base URL과 배포용 Cloud Run Base URL을 BuildConfig 기반으로 분리한다.
- Debug 빌드는 기본적으로 `http://10.0.2.2:8080/`를 사용하고, Release 빌드는 `https://cryptoview-api-620339426938.asia-northeast3.run.app/`를 사용하도록 설정한다.
- 실기기 로컬 테스트를 위해 `local.properties`의 `debug.backend.base.url` 값으로 Debug 백엔드 URL을 쉽게 덮어쓸 수 있게 한다.
- 백엔드 API들은 단일 `@BackendRetrofit` 인스턴스를 공유하게 하고, Retrofit endpoint annotation은 앞 슬래시 없이 `api/...` 형식으로 통일한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest :app:compileReleaseKotlin`를 실행해 Debug 테스트와 Release Kotlin 컴파일을 함께 확인한다.
- 생성된 BuildConfig에서 Debug는 `http://10.0.2.2:8080/`, Release는 Cloud Run URL을 사용하는지 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 테스트 실행 후 생성된 `.kotlin` 빌드 산출물은 작업 변경에 포함되지 않도록 정리한다.

## 기능 관련 프롬프트
- Android Emulator에서 `http://10.0.2.2:8080/` 로컬 백엔드 호출 시 발생하는 `CLEARTEXT communication to 10.0.2.2 not permitted by network security policy` 오류를 debug 빌드 전용 설정으로 해결한다.
- `app/src/debug`에 debug 전용 AndroidManifest와 network security config를 추가해 `10.0.2.2`, `localhost`에 대해서만 cleartext HTTP 통신을 허용한다.
- main/release manifest에는 `usesCleartextTraffic=true`를 넣지 않고, release 빌드는 기존 Cloud Run HTTPS 통신만 사용하게 유지한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat processDebugMainManifest processReleaseMainManifest`를 실행해 debug/release manifest merge를 확인한다.
- Debug merged manifest에는 `android:networkSecurityConfig="@xml/network_security_config"`가 포함되고, Release merged manifest에는 cleartext/networkSecurityConfig 설정이 없는지 확인한다.

## 기타 프롬프트
- 기존 Retrofit, OkHttp, FirebaseAuthInterceptor 구조와 API 경로는 변경하지 않는다.
- 테스트 실행 후 생성된 `.kotlin` 빌드 산출물은 작업 변경에 포함되지 않도록 정리한다.

## 기능 관련 프롬프트
- 거래소 Private API용 백엔드 URL과 AI 분석 API용 백엔드 URL을 분리한다.
- 거래소 백엔드는 Debug/Release 모두 Cloud Run URL을 사용하고, AI 백엔드는 Debug에서만 로컬 `http://10.0.2.2:8080/`을 사용할 수 있게 한다.
- BuildConfig를 `EXCHANGE_BACKEND_BASE_URL`, `AI_BACKEND_BASE_URL`로 분리하고, NetworkModule에는 `@ExchangeRetrofit`, `@AiRetrofit` Qualifier를 추가한다.
- Upbit/Gate.io 계정 조회, 평단 조회, credential 저장/삭제 API는 `@ExchangeRetrofit`을 사용하고, AI 포트폴리오 분석 API만 `@AiRetrofit`을 사용하게 한다.
- 실기기 로컬 AI 테스트를 위해 `local.properties`의 `debug.ai.backend.base.url` 값으로 Debug AI URL을 덮어쓸 수 있게 한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest :app:compileReleaseKotlin`를 실행해 Debug 테스트와 Release Kotlin 컴파일을 확인한다.
- 생성된 BuildConfig에서 Debug는 거래소 Cloud Run/AI 로컬 URL, Release는 거래소와 AI 모두 Cloud Run URL을 사용하는지 확인한다.
- Debug merged manifest에는 networkSecurityConfig가 포함되고 Release merged manifest에는 cleartext 관련 설정이 없는지 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.
- 테스트 실행 후 생성된 `.kotlin` 빌드 산출물은 작업 변경에 포함되지 않도록 정리한다.

## 기능 관련 프롬프트
- README를 최신 기능 기준으로 정리한다.
- AI 포트폴리오 분석 기능, AI 분석 다이얼로그, 5분 메모리 캐시, 거래소/AI 백엔드 URL 분리 정책을 문서에 반영한다.
- Debug에서는 AI API만 로컬 백엔드를 사용하고 거래소 Private API는 Debug/Release 모두 Cloud Run을 사용한다는 정책을 명확히 설명한다.
- debug 전용 cleartext 설정, 실기기 로컬 AI 테스트 방법, 주요 백엔드 API 경로, 보안 정책, 테스트 명령을 README에 추가한다.

## 테스트 관련 프롬프트
- README 문서 변경만 수행했으므로 빌드/테스트는 새로 실행하지 않고, 문서 내 주요 키워드와 섹션이 반영됐는지 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.

## 기능 관련 프롬프트
- 설정 화면의 연동된 거래소 목록에서 Upbit/Gate.io를 개별로 연동 해제할 수 있는 버튼과 확인 다이얼로그를 추가한다.
- 개별 연동 해제는 기존 `ExchangeSettingsViewModel.deleteCredentials(exchange)` 흐름을 사용해 백엔드 credential 삭제와 로컬 연동 marker 삭제를 함께 처리한다.
- 연동 해제 중에는 중복 클릭을 막고, 기존 설정 화면의 카드/텍스트 버튼 톤을 유지한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 설정 화면 UI 수정 후 Kotlin/Compose 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 깨진 문자열 중 컴파일을 막는 설정 화면 문구는 정상 한글 문자열로 정리한다.
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.

## 기능 관련 프롬프트
- 설정 화면에서 다이얼로그 UI를 하위 컴포넌트 디렉터리로 분리한다.
- 로그아웃 확인, 거래소 개별 연동 해제 확인, 거래소 연동 입력 다이얼로그를 `presentation/settings/dialog` 패키지로 이동한다.
- `SettingsScreen`은 상태 보유와 ViewModel 이벤트 연결만 담당하고, 다이얼로그 내부 UI는 별도 컴포저블이 담당하도록 정리한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 설정 다이얼로그 분리 후 Kotlin/Compose 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일은 사용자 요청 범위가 아니므로 수정하지 않는다.

## 기능 관련 프롬프트
- 포트폴리오 문서에서 Android 관련 부분만 최신 구현 기준으로 정리한다.
- README를 Android 앱 중심 문서로 재구성하고, React Native와 백엔드 전체 설명은 제외한다.
- Google 로그인, Upbit/Gate.io 백엔드 credential 연동, Gate.io Spot 평균단가 조회, 자산 개요, 보유 목록, 코인 상세, AI 포트폴리오 요약, 설정 기능을 현재 Android 구현 기준으로 반영한다.
- `EXCHANGE_BACKEND_BASE_URL`, `AI_BACKEND_BASE_URL`, `GATE_BASE_URL` 분리 정책과 Gate.io 평균단가 API 경로를 문서화한다.
- Android 앱이 거래소 accessKey/secretKey를 평균단가 요청에 포함하지 않고 Firebase ID Token만 사용하는 구조를 명확히 기록한다.

## 테스트 관련 프롬프트
- README 문서 변경이므로 Gradle 빌드/테스트는 새로 실행하지 않는다.
- `git diff --check`로 공백 오류를 확인한다.
- README와 상세 화면 파일에 깨진 문자 패턴이 새로 들어가지 않았는지 검색한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- AI 포트폴리오 분석 요청 계약을 `portfolioSummary`와 `holdings` 구조로 변경한다.
- `portfolioSummary`에는 `baseCurrency`, `holdingsCount`, `totalValuationKrw`, `totalPnlKrw`, `totalPnlRate`를 담는다.
- `holdings`에는 평단이 있는 보유 자산만 포함한다.
- 보유 자산 항목에는 `symbol`, `market`, `quantity`, `averagePrice`, `currentPrice`, `valuationKrw`, `pnlKrw`, `pnlRate`를 담는다.
- Upbit market은 `KRW-{SYMBOL}`, Gate.io market은 `{SYMBOL}_USDT` 형식으로 생성한다.

## 테스트 관련 프롬프트
- AI snapshot mapper 테스트를 새 계약에 맞게 수정한다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 요청 계약 변경 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 백엔드가 새 계약에 맞춰 처리할 수 있도록 Android 요청 구조를 먼저 고정한다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- 설정에서 표시 통화를 USDT로 변경해도 AI 분석글이 KRW 기준으로 생성되는 문제를 수정한다.
- AI 분석 요청 생성 시 현재 표시 통화 설정을 읽어 `portfolioSummary.baseCurrency`에 반영한다.
- 표시 통화가 USDT이면 전체 평가금액, 총 손익, 평균단가, 현재가, 평가금액, 손익을 `usdtKrwRate`로 나눠 USDT 기준 값으로 보낸다.
- 표시 통화가 KRW이면 기존 KRW 기준 값을 그대로 보낸다.
- `baseCurrency`가 KRW/USDT로 달라질 수 있으므로 금액 필드명은 `totalValuation`, `totalPnl`, `valuation`, `pnl`처럼 통화 중립 이름으로 변경한다.

## 테스트 관련 프롬프트
- AI snapshot mapper 테스트에 USDT 표시 통화일 때 금액이 환율로 나뉘는 케이스를 추가한다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 표시 통화 반영 후 Kotlin/Hilt 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- AI 분석 요청에서 갑자기 400이 발생하는 원인을 점검한다.
- 백엔드가 아직 기존 `holdings` 필드를 필수로 검증할 가능성이 있으므로, 새 `holdingsWithAveragePrice` 필드와 함께 호환용 `holdings` 필드도 동일한 평단 보유 목록으로 전송한다.
- 요약 필드와 평단 보유 항목 중심 구조는 유지하면서 기존 백엔드 요청 검증과의 호환성을 확보한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 AI 요청 DTO 변경 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- 파일 수가 많아져 찾기 어려운 문제를 줄이기 위해 AI 관련 파일을 하위 디렉터리로 분류한다.
- `presentation/component/assetsOverview` 아래 AI 상태, ViewModel, 다이얼로그를 `ai` 하위 패키지로 이동한다.
- AI 포트폴리오 생성 UseCase를 `domain/usecase/ai`로 이동한다.
- AI snapshot mapper를 `domain/mapper/ai`로 이동한다.
- AI remote mapper를 `data/remote/mapper/ai`로 이동한다.
- 파일 이동 후 package 선언과 import를 새 구조에 맞게 갱신한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 파일 이동과 package/import 변경 후 Kotlin/Compose 컴파일 및 단위 테스트 통과 여부를 확인한다.
- `git diff --check`로 공백 오류를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.
- 비어 있는 기존 `assetsOverview/dialog` 디렉터리는 정리했다.

## 기능 관련 프롬프트
- 백엔드로 기능을 옮긴 뒤 Android에 남은 불필요한 DTO, API, credential 관련 코드를 전체적으로 점검한다.
- Android 로컬에 Binance/Bybit API Key를 저장하던 이전 credential 구조를 제거한다.
- `CredentialsProvider`, `ExchangeCredentials`, `SecureStorage`처럼 로컬 API Key 복호화/메모리 캐시에만 쓰이던 코드를 제거한다.
- `CredentialsManager`는 Upbit/Gate.io 연동 여부 marker만 관리하도록 단순화한다.
- Gate.io futures private API를 Android에서 직접 호출하던 DTO/API/Repository/UseCase/domain model을 제거한다.
- 향후 Gate.io futures가 필요하면 백엔드 credential 구조 기반 API로 다시 붙이는 전제로 현재 직접 호출 코드를 정리한다.

## 테스트 관련 프롬프트
- DTO/API/credential 관련 참조 검색으로 제거한 타입의 남은 사용처가 없는지 확인한다.
- DTO 클래스별 사용량을 다시 확인해 남은 DTO들이 API 또는 mapper에 연결되어 있는지 점검한다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 제거 후 Kotlin/Hilt 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- AI 분석 다이얼로그에서 수행하던 분석글 trim, replace, 필터, 문단 분리 로직을 ViewModel로 이동한다.
- `AiPortfolioInsightUiState.Success`가 원본 insight와 함께 표시용 문단 리스트를 가지도록 변경한다.
- 다이얼로그는 문자열 가공 없이 ViewModel이 만든 문단 리스트만 렌더링하도록 단순화한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 ViewModel 상태 변경과 다이얼로그 렌더링 변경 후 Kotlin/Compose 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- AI 포트폴리오 분석 요청 snapshot을 전체 요약 지표 중심으로 구성한다.
- `baseCurrency`, `holdingsCount`, `totalValuationKrw`, `totalPnlKrw`, `totalPnlRate`를 메인 요약 필드로 보낸다.
- 코인별 상세 데이터는 평단이 있는 보유 자산만 `holdingsWithAveragePrice` 배열로 묶어서 보낸다.
- 평단이 없는 보유 자산은 AI 상세 배열에서 제외하되, 전체 보유 개수와 총 평가금액/총 손익 계산에는 포함한다.

## 테스트 관련 프롬프트
- AI snapshot mapper 테스트를 추가해 전체 보유 개수는 유지되고 평단 있는 보유 항목만 상세 배열에 포함되는지 검증한다.
- `.\gradlew.bat testDebugUnitTest`를 실행해 snapshot DTO 변경 후 Kotlin 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.

## 기능 관련 프롬프트
- AI 포트폴리오 분석 다이얼로그의 가독성이 떨어지므로 다이얼로그 세로 공간을 위아래 10dp씩 더 확보한다.
- AI 분석 본문을 하나의 긴 텍스트로 표시하지 않고 문단 단위로 나눠 표시한다.
- 본문 줄간격과 문단 간격을 키워 긴 분석글을 읽기 쉽게 만든다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 AI 분석 다이얼로그 UI 변경 후 Kotlin/Compose 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.
- PowerShell 출력에서 일부 한글이 깨져 보일 수 있으나, `git diff`와 검색 기준으로 README 본문은 정상 한글로 확인했다.

## 기능 관련 프롬프트
- 설정 화면의 연동된 거래소 목록에서 `연동됨` 텍스트를 제거한다.
- 거래소명 왼쪽 시작점에 초록색 상태 점을 표시해 연동 상태를 더 간결하게 보여준다.
- 기존 거래소 해제 버튼 동작은 유지한다.

## 테스트 관련 프롬프트
- `.\gradlew.bat testDebugUnitTest`를 실행해 설정 화면 UI 변경 후 Kotlin/Compose 컴파일과 단위 테스트 통과 여부를 확인한다.

## 기타 프롬프트
- 작업 전 현재 브랜치와 변경 파일을 확인했다.
- 기존 `.idea` 변경 파일과 `docs` 추가 파일은 사용자 요청 범위가 아니므로 수정하지 않았다.
