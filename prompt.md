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
