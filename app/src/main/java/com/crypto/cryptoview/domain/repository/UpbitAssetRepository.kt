package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.UpbitAccountBalance

interface UpbitAssetRepository {
    suspend fun getAccountBalances(): Result<List<UpbitAccountBalance>>
}