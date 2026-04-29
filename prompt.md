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
