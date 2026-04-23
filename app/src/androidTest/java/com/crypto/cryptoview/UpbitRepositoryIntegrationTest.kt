package com.crypto.cryptoview

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.crypto.cryptoview.data.remote.api.FetchUpbitAssets
import com.crypto.cryptoview.data.repository.upbitRepoImpl.UpbitRepositoryImpl
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
 * UpbitRepositoryImpl 통합 테스트 (백엔드 API 경유)
 * 실행 전: 기기에서 Firebase 로그인이 완료된 상태여야 합니다.
 */
@RunWith(AndroidJUnit4::class)
class UpbitRepositoryIntegrationTest {

    private lateinit var repository: UpbitRepositoryImpl

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Before
    fun setUp() = runBlocking {
        val token = getFirebaseToken()

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor { chain ->
                val req = if (!token.isNullOrBlank()) {
                    chain.request().newBuilder().header("Authorization", "Bearer $token").build()
                } else chain.request()
                chain.proceed(req)
            }
            .build()

        val api = Retrofit.Builder()
            .baseUrl("https://cryptoview-api-620339426938.us-central1.run.app/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FetchUpbitAssets::class.java)

        repository = UpbitRepositoryImpl(api)
    }

    private suspend fun getFirebaseToken(): String? {
        val auth = FirebaseAuth.getInstance()
        return try {
            auth.currentUser?.getIdToken(false)?.await()?.token
                ?: auth.signInAnonymously().await().user?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            println("Firebase 토큰 획득 실패: ${e.message}")
            null
        }
    }

    @Test
    fun testGetAccountBalances_succeeds() = runTest {
        val result = repository.getAccountBalances()

        println("=== getAccountBalances 결과 ===")
        result.onSuccess { balances ->
            println("✅ 성공: ${balances.size}개")
            balances.forEach { println("  ${it.currency}: ${it.balance}") }
            Assert.assertTrue("계좌가 1개 이상 있어야 함", balances.isNotEmpty())
        }
        result.onFailure { e ->
            println("❌ 실패: ${e.message}")
            Assert.fail("API 호출 실패: ${e.message}")
        }
    }

    @Test
    fun testGetAccountBalances_validFormat() = runTest {
        val result = repository.getAccountBalances()
        result.onSuccess { balances ->
            balances.forEach { balance ->
                Assert.assertTrue(
                    "currency 형식 오류: ${balance.currency}",
                    balance.currency.matches(Regex("[A-Z]{2,10}"))
                )
                Assert.assertTrue("balance >= 0", balance.balance >= 0.0)
                Assert.assertTrue("locked >= 0", balance.locked >= 0.0)
                Assert.assertTrue("avgBuyPrice >= 0", balance.avgBuyPrice >= 0.0)
            }
        }
    }
}