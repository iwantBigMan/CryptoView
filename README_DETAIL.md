# CryptoView - 암호화폐 자산 관리 앱

## 📱 주요 기능

### 1. 멀티 거래소 지원
- **Upbit** (업비트)
- **Gate.io** (게이트아이오)
- Binance (바이낸스) - 준비 중
- Bybit (바이비트) - 준비 중

### 2. 자산 개요 (Assets Overview)
- 전체 자산 총액 실시간 업데이트
- 거래소별 자산 분포 도넛 차트
- Top 5 보유 코인 리스트
- 수익률 표시 (금액 & 퍼센트)

### 3. 보유 코인 상세 (Holdings)
- 거래소별 코인 보유 현황
- 검색 및 정렬 기능
  - 금액순, 수익률순, 심볼순
- 1원 이하 자산 자동 필터링
- 코인별 상세 정보 화면

### 4. 로그인 & 보안
- API Key 기반 인증
- DataStore를 사용한 안전한 저장
- 거래소별 개별 연동/해제
- 로그아웃 기능

### 5. 설정 (Settings)
- 연동된 거래소 목록 확인
- 로그아웃 기능
- 앱 정보

---

## 🏗️ 아키텍처

### Clean Architecture 준수
```
presentation/     (UI Layer - Compose)
  ├── login/      (로그인 화면)
  ├── component/  (메인 화면 컴포넌트)
  └── main/       (메인 액티비티 & 네비게이션)

domain/           (Domain Layer)
  ├── model/      (도메인 모델)
  └── usecase/    (비즈니스 로직)
      └── calculator/  (자산 계산 로직)

data/             (Data Layer)
  ├── remote/     (API 통신)
  │   ├── api/
  │   ├── dto/
  │   └── interceptor/
  ├── repository/
  └── local/      (DataStore)

di/               (의존성 주입 - Hilt)
```

### 기술 스택
- **UI**: Jetpack Compose
- **DI**: Hilt
- **비동기**: Coroutines + Flow
- **네트워크**: Retrofit + OkHttp
- **직렬화**: Kotlinx Serialization
- **로컬 저장소**: DataStore Preferences
- **아키텍처**: Clean Architecture + MVVM

---

## 🔐 API Key 발급 방법

### Upbit (업비트)
1. [Upbit 웹사이트](https://upbit.com) 로그인
2. 마이페이지 → Open API 관리
3. API 키 발급
   - ⚠️ **출금 권한은 체크하지 마세요**
   - 자산 조회 권한만 부여
4. Access Key와 Secret Key 복사

### Gate.io (게이트아이오)
1. [Gate.io 웹사이트](https://www.gate.io) 로그인
2. 계정 → API Management
3. Create API Key
   - ⚠️ **Withdrawal 권한은 체크하지 마세요**
   - Read Only 권한만 부여
4. API Key와 Secret Key 복사

---

## 🚀 실행 방법

### 1. 클론 및 빌드
```bash
git clone <repository-url>
cd cryptoView
./gradlew build
```

### 2. API Key 설정 (옵션)
앱 내에서 로그인 화면을 통해 API Key를 직접 입력할 수 있습니다.

또는 개발 시 `local.properties`에 추가:
```properties
UPBIT_ACCESS_KEY=your_upbit_access_key
UPBIT_SECRET_KEY=your_upbit_secret_key
GATE_IO_API_KEY=your_gateio_api_key
GATE_IO_SECRET_KEY=your_gateio_secret_key
```

### 3. 실행
Android Studio에서 Run 버튼 클릭

---

## 📊 주요 기능 상세

### 자산 계산 로직
- **UpbitCalculator**: 업비트 원화 자산 계산
- **ForeignCalculator**: 해외 거래소 USDT 자산 → 원화 변환
- **ExchangeRateProvider**: USDT/KRW 환율 실시간 조회 (업비트 기준)

#### 계산 흐름
1. 거래소별 잔고 조회 (API)
2. 코인별 현재가 조회 (API)
3. USDT/KRW 환율 조회 (업비트 시세 기준)
4. 자산 가치 계산 (잔고 × 현재가 × 환율)
5. 수익률 계산 (현재가 - 평단가)

### 실시간 업데이트
- 1초마다 자동 갱신
- 화면 전환 시 자동 중지/재시작
- 네트워크 오류 시 재시도

---

## 🔒 보안

- API Key는 **기기 내부에만 저장**
- DataStore Preferences 사용 (암호화)
- 네트워크 통신 시 HTTPS 사용
- 출금 권한 불필요 (Read Only)

---

## 📝 TODO

- [ ] Binance 연동
- [ ] Bybit 선물 포지션 조회
- [ ] 김프(김치 프리미엄) 계산 및 표시
- [ ] 차트 추가 (가격 변동 그래프)
- [ ] 알림 기능 (목표가 도달 시)
- [ ] 다크/라이트 테마 전환
- [ ] 통화 단위 선택 (KRW/USDT)

---

## 📄 라이센스

이 프로젝트는 개인 포트폴리오 목적으로 제작되었습니다.

---

## 📧 문의

이슈 또는 질문이 있으시면 GitHub Issues에 남겨주세요.
