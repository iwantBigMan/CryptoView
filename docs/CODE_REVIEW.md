# 📝 코드 리뷰 - 개선이 필요한 코드 분석

## 가장 마음에 들지 않는 코드

### 📍 코드 위치
`LoginViewModel.kt` - `saveSelectedCredentials()` 메서드

### 📄 현재 코드
```kotlin
fun saveSelectedCredentials() {
    val currentState = _uiState.value
    val toSave = currentState.selectedExchanges.toMutableSet().apply { add(ExchangeType.UPBIT) }

    // 입력 검증 루프
    for (ex in toSave) {
        val input = currentState.inputs[ex]
        if (input == null || input.apiKey.isBlank() || input.secretKey.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "${ex.displayName}의 API Key/Secret을 입력해주세요")
            return
        }
    }

    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            // 1️⃣ 검증 루프 - when 분기
            for (ex in toSave) {
                val input = _uiState.value.inputs[ex] ?: ExchangeInput()
                val valid = when (ex) {
                    ExchangeType.UPBIT -> validateUpbit(input.apiKey, input.secretKey)
                    ExchangeType.GATEIO -> validateGate(input.apiKey, input.secretKey)
                    else -> true  // ⚠️ 검증 누락 가능
                }
                if (!valid) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "${ex.displayName} 연동 검증 실패...")
                    return@launch
                }
            }

            // 2️⃣ 저장 루프 - 중복된 when 분기
            credentialsManager.clearAllCredentials()  // ⚠️ 먼저 삭제
            val stateNow = _uiState.value
            for (ex in toSave) {
                val input = stateNow.inputs[ex] ?: ExchangeInput()
                when (ex) {
                    ExchangeType.UPBIT -> credentialsManager.saveUpbitCredentials(input.apiKey, input.secretKey)
                    ExchangeType.GATEIO -> credentialsManager.saveGateioCredentials(input.apiKey, input.secretKey)
                    ExchangeType.BINANCE -> credentialsManager.saveBinanceCredentials(input.apiKey, input.secretKey)
                    ExchangeType.BYBIT -> credentialsManager.saveBybitCredentials(input.apiKey, input.secretKey)
                }
            }

            _uiState.value = _uiState.value.copy(isLoading = false, selectedExchanges = emptySet(), loginSuccess = true)
        } catch (e: Throwable) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "저장 실패: ${e.message}")
        }
    }
}
```

---

## ❌ 문제점 분석

### 1. OCP(개방-폐쇄 원칙) 위반
| 문제 | 설명 |
|:---|:---|
| **when 분기 중복** | 검증 루프와 저장 루프에서 동일한 `when` 분기가 반복됨 |
| **확장 시 수정 필요** | 새 거래소 추가 시 두 곳의 `when` 분기를 모두 수정해야 함 |
| **else -> true 위험** | 새 거래소 추가 시 검증 로직을 빠뜨려도 컴파일 에러가 발생하지 않아 버그로 이어질 수 있음 |

### 2. 원자성(Atomicity) 미보장
| 문제 | 설명 |
|:---|:---|
| **삭제 후 저장** | `clearAllCredentials()` 호출 후 저장하는 구조 |
| **부분 실패 가능** | 중간에 실패하면 일부 거래소 키만 저장되는 불일치 상태 발생 |
| **롤백 불가** | 실패 시 이전 상태로 복구하는 로직 없음 |

### 3. 상태 업데이트 패턴 반복
```kotlin
// 동일한 패턴이 여러 번 반복됨
_uiState.value = _uiState.value.copy(isLoading = true, error = null)
_uiState.value = _uiState.value.copy(isLoading = false, error = "...")
_uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
```

---

## 🔧 제약 사항 (왜 이렇게 작성했나?)

| 제약 | 설명 |
|:---|:---|
| **초기 설계** | CredentialsManager 구현 시 거래소별 메서드를 분리하는 구조로 설계 |
| **범위 가정** | 거래소 수가 제한적(4개)인 범위 내에서 관리될 것이라 가정하고 단순화 |
| **요구사항 변경** | 이후 거래소 추가 요구가 생기면서 분기 기반 확장이 반복되는 구조가 됨 |
| **시간 제약** | MVP 구현 우선 → 리팩토링 시간 부족 |

---

## ✅ 개선 방안

### 1. Strategy + Map 패턴으로 when 분기 제거

```kotlin
// 거래소별 검증/저장 전략 인터페이스
interface ExchangeStrategy {
    suspend fun validate(apiKey: String, secretKey: String): Boolean
    suspend fun save(apiKey: String, secretKey: String)
}

// 구현체 예시
class UpbitStrategy @Inject constructor(
    private val validateUseCase: ValidateUpbitCredentialsUseCase,
    private val credentialsManager: CredentialsManager
) : ExchangeStrategy {
    override suspend fun validate(apiKey: String, secretKey: String): Boolean {
        return validateUseCase(apiKey, secretKey).isSuccess
    }
    override suspend fun save(apiKey: String, secretKey: String) {
        credentialsManager.saveUpbitCredentials(apiKey, secretKey)
    }
}

// ViewModel에서 Map 주입
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val strategies: Map<ExchangeType, @JvmSuppressWildcards ExchangeStrategy>
) : ViewModel() {
    
    fun saveSelectedCredentials() {
        viewModelScope.launch {
            // when 분기 없이 처리
            for (ex in toSave) {
                val strategy = strategies[ex] ?: error("Unknown exchange: $ex")
                if (!strategy.validate(...)) return@launch
            }
            strategies.forEach { (ex, strategy) -> strategy.save(...) }
        }
    }
}
```

### 2. 트랜잭션 패턴으로 원자성 보장

```kotlin
suspend fun saveAllOrNothing(credentials: List<Pair<ExchangeType, ExchangeInput>>) {
    // 1단계: 모든 검증 먼저 수행
    val validationResults = credentials.map { (ex, input) ->
        ex to strategies[ex]?.validate(input.apiKey, input.secretKey)
    }
    
    // 2단계: 모두 성공한 경우에만 저장
    if (validationResults.all { it.second == true }) {
        credentialsManager.clearAllCredentials()
        credentials.forEach { (ex, input) ->
            strategies[ex]?.save(input.apiKey, input.secretKey)
        }
    } else {
        // 실패한 거래소 정보 반환
        val failed = validationResults.filter { it.second != true }
        throw ValidationException(failed.map { it.first })
    }
}
```

### 3. MVI 패턴으로 상태 관리 개선

```kotlin
// Intent (사용자 액션)
sealed class LoginIntent {
    data class SaveCredentials(val exchanges: Set<ExchangeType>) : LoginIntent()
    object Logout : LoginIntent()
}

// Reducer 함수로 상태 변환 중앙화
private fun reduce(state: LoginUiState, result: LoginResult): LoginUiState {
    return when (result) {
        is LoginResult.Loading -> state.copy(isLoading = true, error = null)
        is LoginResult.Success -> state.copy(isLoading = false, loginSuccess = true)
        is LoginResult.Error -> state.copy(isLoading = false, error = result.message)
    }
}
```

---

## 📊 개선 효과

| Before | After |
|:---|:---|
| 새 거래소 추가 시 2곳 수정 | Strategy 클래스 1개만 추가 |
| else -> true로 검증 누락 가능 | Map에 없으면 즉시 에러 |
| 부분 저장 상태 발생 가능 | 모든 검증 성공 후에만 저장 |
| 상태 업데이트 코드 중복 | reduce() 함수로 중앙화 |

---

## 📅 작성일
2025년 3월 4일

## 🏷️ 관련 파일
- `presentation/login/LoginViewModel.kt`
- `data/local/CredentialsManager.kt`

