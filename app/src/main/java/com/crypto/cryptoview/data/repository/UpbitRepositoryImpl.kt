package com.crypto.cryptoview.data.repository

import com.crypto.cryptoview.data.remote.api.UpbitApi
import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.repository.UpbitAssetRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class UpbitRepositoryImpl @Inject constructor(
    private val api: UpbitApi
) : UpbitAssetRepository {
    override suspend fun getAccountBalances(): Result<List<UpbitAccountBalance>> = runCatching {
        api.getUpbitAccountBalances().map { dto -> dto.toDomain() }
    }
}