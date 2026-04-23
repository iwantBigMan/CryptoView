package com.crypto.cryptoview.data.repository.upbitRepoImpl

import com.crypto.cryptoview.data.remote.api.FetchUpbitAssets
import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.repository.UpbitAssetRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpbitRepositoryImpl @Inject constructor(
    private val api: FetchUpbitAssets
) : UpbitAssetRepository {
    override suspend fun getAccountBalances(): Result<List<UpbitAccountBalance>> = runCatching {
        api.fetchAssets().map { it.toDomain() }
    }
}