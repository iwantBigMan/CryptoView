package com.crypto.cryptoview.di

import com.crypto.cryptoview.BuildConfig
import com.crypto.cryptoview.data.auth.FirebaseTokenProvider
import com.crypto.cryptoview.data.auth.FirebaseTokenProviderImpl
import com.crypto.cryptoview.data.remote.api.GateSpotApi
import com.crypto.cryptoview.data.remote.api.UpbitMarketApi
import com.crypto.cryptoview.data.remote.api.UpbitTickerAllApi
import com.crypto.cryptoview.data.remote.interceptor.AccountResponseLoggingInterceptor
import com.crypto.cryptoview.data.remote.interceptor.FirebaseAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

// Qualifier 정의
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UpbitClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GateIoClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExchangeRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AiRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideFirebaseTokenProvider(): FirebaseTokenProvider {
        return FirebaseTokenProviderImpl()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // 공통 OkHttpClient 생성 함수
    private fun createOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: okhttp3.Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 공통 Retrofit 생성 함수
    private inline fun <reified T> createApiService(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        json: Json
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(T::class.java)
    }

    // === Upbit ===
    @Provides
    @Singleton
    @UpbitClient
    fun provideUpbitOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AccountResponseLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideUpbitMarketApi(
        @UpbitClient okHttpClient: OkHttpClient,
        json: Json
    ): UpbitMarketApi {
        return createApiService("https://api.upbit.com/", okHttpClient, json)
    }

    @Provides
    @Singleton
    fun provideUpbitTickerAllApi(
        @UpbitClient okHttpClient: OkHttpClient,
        json: Json
    ): UpbitTickerAllApi {
        return createApiService("https://api.upbit.com/", okHttpClient, json)
    }

    // 백엔드 공용 OkHttpClient (Firebase 토큰 자동 주입)
    @Provides
    @Singleton
    fun provideBackendOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        tokenProvider: FirebaseTokenProviderImpl
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(FirebaseAuthInterceptor(tokenProvider))
            .addInterceptor(AccountResponseLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 백엔드 Retrofit 인스턴스
    @Provides
    @Singleton
    @ExchangeRetrofit
    fun provideExchangeRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.EXCHANGE_BACKEND_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @AiRetrofit
    fun provideAiRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AI_BACKEND_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // 백엔드 업비트 검증 API
    @Provides
    @Singleton
    fun provideValidateAndSaveUpbitApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.ValidateAndSaveUpbit {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.ValidateAndSaveUpbit::class.java)
    }

    @Provides
    @Singleton
    fun provideFetchUpbitAssetsApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.FetchUpbitAssets {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.FetchUpbitAssets::class.java)
    }

    @Provides
    @Singleton
    fun provideDeleteUpbitCredentialsApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.DeleteUpbitCredentials {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.DeleteUpbitCredentials::class.java)
    }

    @Provides
    @Singleton
    fun provideValidateAndSaveGateIoApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.ValidateAndSaveGateIo {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.ValidateAndSaveGateIo::class.java)
    }

    @Provides
    @Singleton
    fun provideFetchGateIoAccountsApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.FetchGateIoAccounts {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.FetchGateIoAccounts::class.java)
    }

    @Provides
    @Singleton
    fun provideDeleteGateIoCredentialApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.DeleteGateIoCredential {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.DeleteGateIoCredential::class.java)
    }

    @Provides
    @Singleton
    fun provideFetchGateIoSpotAveragePriceApi(
        @ExchangeRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.FetchGateIoSpotAveragePrice {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.FetchGateIoSpotAveragePrice::class.java)
    }

    @Provides
    @Singleton
    fun provideAiPortfolioInsightApi(
        @AiRetrofit retrofit: Retrofit
    ): com.crypto.cryptoview.data.remote.api.AiPortfolioInsightApi {
        return retrofit.create(com.crypto.cryptoview.data.remote.api.AiPortfolioInsightApi::class.java)
    }

    // Gate.io

    @Provides
    @Singleton
    @GateIoClient
    fun provideGateIoOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideGateSpotApi(
        @GateIoClient okHttpClient: OkHttpClient,
        json: Json
    ): GateSpotApi {
        return createApiService(
            baseUrl = BuildConfig.GATE_BASE_URL,
            okHttpClient = okHttpClient,
            json = json
        )
    }

}
