# CryptoView

**간단소개**

CryptoView는 여러 거래소(예: Upbit, Binance)의 API를 연동해 개인 보유 자산을 한눈에 확인할 수 있는 모바일 앱(안드로이드)입니다. 사용자는 거래소별 자산 요약, 상위 보유 코인, 각 코인의 상세 포지션을 확인하고 알림을 설정할 수 있습니다. 오프라인 모드와 API 키 관리를 지원합니다.

---

## 핵심 기능 (V1 — 필수)

1. **로그인 / 연동 (ConnectExchanges)**
   - 업비트, 바이낸스 등 거래소 API Key 입력 및 검증
   - 거래소 선택 → API Key/Secret 입력 → 안전한 저장(DataStore)
   - 키 상태: Idle / Validating / Error
   - 오프라인 사용자 경험을 위한 "나중에 연결" 스킵 옵션

2. **홈 대시보드 (Home)**
   - 전체 자산 요약: 총 평가금액, 총 수익률, 오늘 변동
   - 섹션: 거래소별 요약(원형 차트), 상위 보유코인 Top 5
   - 화면 상태: Loading / Success / Error / Empty

3. **보유 코인 리스트 (Holdings)**
   - 코인별 심볼, 수량, 평균매수가(선택), 평가금액, 개별 수익률
   - 정렬(평가금액/수익률), 검색, Pull-to-refresh
   - 성능 고려: `LazyColumn(itemKey = symbol)` 사용

4. **코인 상세 (CoinDetail)**
   - 특정 코인의 합산 포지션(업비트 + 바이낸스 등)
   - 탭 구성: 개요 / 체결·거래내역(선택)
   - 거래소별 보유량·평단·평가, 일·주 단위 수익률
   - 알림 생성: 목표가 도달, 특정 수익률 도달 알림 등

5. **설정 (Settings)**
   - 테마: 라이트 / 다크 / 시스템
   - UI 커스텀: 포인트 컬러, 폰트 크기
   - 백그라운드 동기화(WorkManager): 15m / 30m / 1h
   - 통화(KRW / USDT) 소스 선택
   - API 키 관리(추가/수정/삭제)
   - 알림 권한 및 FCM 토큰 확인
   - 디버그 메뉴: 마지막 동기화 시간, 로그 확인

---

## 아키텍처 & 기술 스택

- **Clean Architecture** (Domain / Data / Presentation)
- **DI:** Dagger Hilt
- **네트워킹:** Retrofit + OkHttp
- **저장소:** DataStore(추후 암호화 적용)
- **UI:** Jetpack Compose

---

## 보안 & 권장사항

- API Key/Secret은 Android Keystore 기반으로 **암호화 저장**
- 민감 데이터 로그 노출 금지
- HTTPS 통신만 사용
- 자동 매매 기능은 없음 (View 전용 앱)

---

## 진행 상황 (Progress) - 현재 업비트만 연동, 게이트 아이오 연동 중

### ✅ 홈 대시보드 (Home) — 완료
- 전체 자산 요약, 거래소별 요약(원형 차트), Top 5 보유코인 UI 구현 완료
- 화면 상태: Loading / Success / Error / Empty 모두 처리

### 🚧 보유 자산 상세 (Holding Coin View) — 진행 중
- 보유 코인 리스트를 LazyColumn 기반으로 구성
- 코인별 자산 정보 아이템 UI 구현 완료

### ✅ 업비트 연동 (Upbit Integration) — 완료
- API Key 입력 → 검증 → 로컬 저장 흐름 구현
- 업비트 잔고 및 체결 정보 조회 기능 완성
- 키 상태 처리(Idle / Validating / Error) 반영
- 기본 예외 처리 및 오류 UI 적용

### ⏳ 게이트아이오 연동 — 진행 중
- REST API 연동 흐름 설계 완료
- 인증 및 시그니처 생성 구현 예정

### ⏳ Holdings / CoinDetail — 일부 개발 완료
- 리스트 기본 UI, 정렬/검색 적용
- CoinDetail 설계 진행 중

### 📝 Settings / 알림 / 백그라운드 동기화 — 설계 단계

---

### 🔜 다음 작업 예정 

- Gate.io 잔고 / 시세 연동 (일부 적용 완료)
- 해외 거래소 자산 USDT → KRW 환산 적용(일부 적용 완료)
- 거래소 앱 딥링크 이동 기능 (자산 클릭 시 해당 거래소 앱으로 이동)
