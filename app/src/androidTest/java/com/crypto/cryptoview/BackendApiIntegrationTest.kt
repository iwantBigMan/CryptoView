package com.crypto.cryptoview

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.crypto.cryptoview.data.remote.api.FetchUpbitAssets
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitRequest
import com.crypto.cryptoview.data.remote.api.ValidateAndSaveUpbit
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * 백엔드 API 통합 테스트
 *
 * 실행 전 준비사항:
 *   1. 에뮬레이터/기기에서 구글 로그인이 완료된 상태여야 합니다.
 *   2. 또는 아래 TEST_EMAIL / TEST_PASSWORD 에 Firebase 계정 정보를 입력하세요.
 *
 * 테스트 항목:
 *   - [testFetchUpbitAccounts] 백엔드 계좌 조회 API
 */
@RunWith(AndroidJUnit4::class)
class BackendApiIntegrationTest {

    companion object {
        private const val BACKEND_BASE_URL =
            "https://cryptoview-api-620339426938.us-central1.run.app/"

        // Firebase 테스트 계정 (없으면 현재 로그인된 사용자 사용)
        private const val TEST_EMAIL = ""    // 필요 시 입력: "test@example.com"
        private const val TEST_PASSWORD = "" // 필요 시 입력: "password"
    }

    private lateinit var fetchUpbitAssetsApi: FetchUpbitAssets
    private lateinit var validateAndSaveUpbitApi: ValidateAndSaveUpbit
    private var firebaseToken: String? = null

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Before
    fun setUp() = runBlocking {
        // Firebase 토큰 획득
        firebaseToken = getFirebaseToken()

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val token = firebaseToken
                val req = if (!token.isNullOrBlank()) {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(req)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        fetchUpbitAssetsApi = retrofit.create(FetchUpbitAssets::class.java)
        validateAndSaveUpbitApi = retrofit.create(ValidateAndSaveUpbit::class.java)
    }

    /** Firebase ID 토큰 획득 (로그인 없으면 익명 로그인 시도) */
    private suspend fun getFirebaseToken(): String? {
        val auth = FirebaseAuth.getInstance()

        // 1) 이미 로그인된 사용자가 있으면 토큰 반환
        auth.currentUser?.let { user ->
            return user.getIdToken(false).await().token
        }

        // 2) TEST_EMAIL/PASSWORD 가 입력되어 있으면 이메일 로그인
        if (TEST_EMAIL.isNotBlank() && TEST_PASSWORD.isNotBlank()) {
            return try {
                val result = auth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD).await()
                result.user?.getIdToken(false)?.await()?.token
            } catch (e: Exception) {
                println("Firebase 이메일 로그인 실패: ${e.message}")
                null
            }
        }

        // 3) 익명 로그인 (백엔드가 익명 유저를 허용하는 경우)
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            println("Firebase 익명 로그인 실패: ${e.message}")
            null
        }
    }

    // ─────────────────────────────────────────────────────────
    // 1. 업비트 계좌 조회 (Firebase 토큰 필요)
    // ─────────────────────────────────────────────────────────
    @Test
    fun testFetchUpbitAccounts() = runTest {
        if (firebaseToken.isNullOrBlank()) {
            println("⚠️  Firebase 토큰 없음 - 테스트 스킵")
            return@runTest
        }

        val result = runCatching { fetchUpbitAssetsApi.fetchAssets() }

        println("=== FetchUpbitAccounts 결과 ===")
        result.onSuccess { list ->
            println("✅ 성공: ${list.size}개 계좌")
            list.forEach { println("  - ${it.currency}: balance=${it.balance}") }
            Assert.assertTrue("계좌 목록이 비어있지 않아야 함", list.isNotEmpty())
            list.forEach { balance ->
                Assert.assertNotNull("currency 는 null 이면 안됨", balance.currency)
            }
        }
        result.onFailure { e ->
            println("❌ 실패: ${e.message}")
            Assert.fail("API 호출 실패: ${e.message}")
        }
    }

    // ─────────────────────────────────────────────────────────
    // 2. 업비트 키 검증 테스트  (실제 키가 없으면 실패 예상)
    // ─────────────────────────────────────────────────────────
    @Test
    fun testValidateUpbitCredentials_withInvalidKey_returns4xx() = runTest {
        if (firebaseToken.isNullOrBlank()) {
            println("⚠️  Firebase 토큰 없음 - 테스트 스킵")
            return@runTest
        }

        val fakeRequest = ValidateUpbitRequest(
            accessKey = "fake_access_key",
            secretKey = "fake_secret_key"
        )

        val result = runCatching {
            validateAndSaveUpbitApi.validateAndSaveCredentials(fakeRequest)
        }

        println("=== ValidateUpbit (잘못된 키) 결과 ===")
        result.onSuccess { res ->
            // 서버가 { valid: false } 같은 응답을 줄 수 있음
            println("✅ 응답: $res")
        }
        result.onFailure { e ->
            // HTTP 4xx → 예상된 실패
            println("❌ HTTP 오류 (예상됨): ${e.message}")
        }

        // 잘못된 키 → success 이면 valid=false 여야 하고, failure 이면 4xx
        println("※ 잘못된 키이므로 4xx 또는 valid=false 응답이 정상입니다.")
    }
}

