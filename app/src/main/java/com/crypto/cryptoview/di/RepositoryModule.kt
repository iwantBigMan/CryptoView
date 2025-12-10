package com.crypto.cryptoview.di

import com.crypto.cryptoview.data.repository.UbitMTickerRepositoryImpl
import com.crypto.cryptoview.data.repository.UpbitRepositoryImpl
import com.crypto.cryptoview.domain.repository.UbbitMTickerRepository
import com.crypto.cryptoview.domain.repository.UpbitAssetRepository

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
}