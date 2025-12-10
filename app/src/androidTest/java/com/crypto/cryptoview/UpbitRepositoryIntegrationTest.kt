package com.crypto.cryptoview

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.crypto.cryptoview.data.remote.api.UpbitApi
import com.crypto.cryptoview.data.remote.interceptor.UpbitAuthInterceptor
import com.crypto.cryptoview.data.repository.UpbitRepositoryImpl
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

@RunWith(AndroidJUnit4::class)
class UpbitRepositoryIntegrationTest {

    private lateinit var repository: UpbitRepositoryImpl
    private lateinit var api: UpbitApi

    @Before
    fun setUp() {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(
                UpbitAuthInterceptor(
                    accessKey = BuildConfig.UPBIT_ACCESS_KEY,
                    secretKey = BuildConfig.UPBIT_SECRET_KEY
                )
            )
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://api.upbit.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UpbitApi::class.java)

        repository = UpbitRepositoryImpl(api)
    }

    @Test
    fun testUpbitApiCallSucceeds() = runTest {
        // Given - 실제 업비트 API 호출 준비

        // When - 계좌 잔고 조회
        val result = repository.getAccountBalances()

        // Then - API 호출 성공 및 데이터 검증
        Assert.assertTrue("API call should succeed", result.isSuccess)

        val balances = result.getOrNull()
        Assert.assertNotNull("Balance data should exist", balances)

        balances?.let {
            Assert.assertTrue("Should have at least one account", it.isNotEmpty())
            it.forEach { balance ->
                Assert.assertNotNull("Currency should not be null", balance.currency)
                Assert.assertTrue("Balance should be non-negative", balance.balance >= 0)
            }
        }
    }

    @Test
    fun testUpbitApiReturnsValidBalanceFormat() = runTest {
        // When
        val result = repository.getAccountBalances()

        // Then
        result.onSuccess { balances ->
            balances.forEach { balance ->
                Assert.assertTrue(
                    "Currency should be valid format",
                    balance.currency.matches(Regex("[A-Z]{3,10}"))
                )
                Assert.assertTrue("Balance should be non-negative", balance.balance >= 0.0)
                Assert.assertTrue("Locked amount should be non-negative", balance.locked >= 0.0)
                Assert.assertTrue(
                    "Average buy price should be non-negative",
                    balance.avgBuyPrice >= 0.0
                )
            }
        }
    }
}