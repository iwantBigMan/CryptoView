package com.crypto.cryptoview.data.repository

import com.crypto.cryptoview.model.AccountBalance
import com.crypto.cryptoview.net.Api
import com.crypto.cryptoview.util.UpbitAuthHelper
import jakarta.inject.Inject
import jakarta.inject.Singleton
import com.crypto.cryptoview.BuildConfig

@Singleton
class AssetRepository @Inject constructor(
    private val api: Api
) {

    suspend fun getAccountBalances(): Result<List<AccountBalance>> = runCatching {
        val authToken = UpbitAuthHelper.generateAuthToken(
            accessKey = BuildConfig.UPBIT_ACCESS_KEY,
            secretKey = BuildConfig.UPBIT_SECRET_KEY
        )

        api.getAccountBalances(authToken)
    }
}