package com.crypto.cryptoview.di

import com.crypto.cryptoview.BuildConfig
import com.crypto.cryptoview.data.remote.api.UpbitApi
import com.crypto.cryptoview.data.remote.interceptor.UpbitAuthInterceptor
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
annotation class BinanceClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BybitClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GateIoClient

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
        return createOkHttpClient(
            loggingInterceptor,
            UpbitAuthInterceptor(
                accessKey = BuildConfig.UPBIT_ACCESS_KEY,
                secretKey = BuildConfig.UPBIT_SECRET_KEY
            )
        )
    }

    @Provides
    @Singleton
    fun provideUpbitApi(
        @UpbitClient okHttpClient: OkHttpClient,
        json: Json
    ): UpbitApi {
        return createApiService("https://api.upbit.com/", okHttpClient, json)
    }

    @Provides
    @Singleton
    fun provideUpbitMarketApi(
        @UpbitClient okHttpClient: OkHttpClient,
        json: Json
    ): com.crypto.cryptoview.data.remote.api.UpbitMarketApi {
        return createApiService("https://api.upbit.com/", okHttpClient, json)
    }

    // Gate.io

}