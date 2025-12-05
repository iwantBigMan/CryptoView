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

    // === Upbit ===
    @Provides
    @Singleton
    @UpbitClient
    fun provideUpbitOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(
                UpbitAuthInterceptor(
                    accessKey = BuildConfig.UPBIT_ACCESS_KEY,
                    secretKey = BuildConfig.UPBIT_SECRET_KEY
                )
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideUpbitApi(
        @UpbitClient okHttpClient: OkHttpClient,
        json: Json
    ): UpbitApi {
        return Retrofit.Builder()
            .baseUrl("https://api.upbit.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UpbitApi::class.java)
    }

//    // === Binance ===
//    @Provides
//    @Singleton
//    @BinanceClient
//    fun provideBinanceOkHttpClient(
//        loggingInterceptor: HttpLoggingInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor(
//                BinanceAuthInterceptor(
//                    apiKey = BuildConfig.BINANCE_API_KEY,
//                    secretKey = BuildConfig.BINANCE_SECRET_KEY
//                )
//            )
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideBinanceApi(
//        @BinanceClient okHttpClient: OkHttpClient,
//        json: Json
//    ): BinanceApi {
//        return Retrofit.Builder()
//            .baseUrl("https://api.binance.com/")
//            .client(okHttpClient)
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//            .create(BinanceApi::class.java)
//    }
//
//    // === Bybit ===
//    @Provides
//    @Singleton
//    @BybitClient
//    fun provideBybitOkHttpClient(
//        loggingInterceptor: HttpLoggingInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor(
//                BybitAuthInterceptor(
//                    apiKey = BuildConfig.BYBIT_API_KEY,
//                    secretKey = BuildConfig.BYBIT_SECRET_KEY
//                )
//            )
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideBybitApi(
//        @BybitClient okHttpClient: OkHttpClient,
//        json: Json
//    ): BybitApi {
//        return Retrofit.Builder()
//            .baseUrl("https://api.bybit.com/")
//            .client(okHttpClient)
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//            .create(BybitApi::class.java)
//    }
//
//    // === Gate.io ===
//    @Provides
//    @Singleton
//    @GateIoClient
//    fun provideGateIoOkHttpClient(
//        loggingInterceptor: HttpLoggingInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor(
//                GateIoAuthInterceptor(
//                    apiKey = BuildConfig.GATEIO_API_KEY,
//                    secretKey = BuildConfig.GATEIO_SECRET_KEY
//                )
//            )
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideGateIoApi(
//        @GateIoClient okHttpClient: OkHttpClient,
//        json: Json
//    ): GateIoApi {
//        return Retrofit.Builder()
//            .baseUrl("https://api.gateio.ws/")
//            .client(okHttpClient)
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//            .create(GateIoApi::class.java)
//    }
//}
}