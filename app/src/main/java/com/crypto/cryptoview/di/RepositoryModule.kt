package com.crypto.cryptoview.di

import com.crypto.cryptoview.data.repository.auth.AuthRepositoryImpl
import com.crypto.cryptoview.data.repository.auth.ExchangeCredentialRepositoryImpl
import com.crypto.cryptoview.data.repository.auth.GoogleAuthRepositoryImpl
import com.crypto.cryptoview.data.repository.gateRepoImpl.GateSpotRepositoryImpl
import com.crypto.cryptoview.data.repository.upbitRepoImpl.UbitMTickerRepositoryImpl
import com.crypto.cryptoview.data.repository.upbitRepoImpl.UpbitTickerAllRepositoryimpl
import com.crypto.cryptoview.data.repository.upbitRepoImpl.UpbitRepositoryImpl
import com.crypto.cryptoview.domain.repository.AuthRepository
import com.crypto.cryptoview.domain.repository.ExchangeCredentialRepository
import com.crypto.cryptoview.domain.repository.GoogleAuthRepository
import com.crypto.cryptoview.domain.repository.GateFuturesRepository
import com.crypto.cryptoview.domain.repository.GateSpotRepository
import com.crypto.cryptoview.domain.repository.UbbitMTickerRepository
import com.crypto.cryptoview.domain.repository.UpbitAssetRepository
import com.crypto.cryptoview.domain.repository.UpbitTickerAllRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAssetRepository(
        impl: UpbitRepositoryImpl
    ): UpbitAssetRepository

    @Binds
    @Singleton
    abstract fun bindMTickerRepository(
        impl: UbitMTickerRepositoryImpl
    ): UbbitMTickerRepository



    @Binds
    @Singleton
    abstract fun bindGateSpotRepository(
        impl: GateSpotRepositoryImpl
    ): GateSpotRepository



    @Binds
    @Singleton
    abstract fun bindUpbitTickerAllRepository(
        impl: UpbitTickerAllRepositoryimpl
    ): UpbitTickerAllRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGoogleAuthRepository(
        impl: GoogleAuthRepositoryImpl
    ): GoogleAuthRepository

    @Binds
    @Singleton
    abstract fun bindExchangeCredentialRepository(
        impl: ExchangeCredentialRepositoryImpl
    ): ExchangeCredentialRepository
}
