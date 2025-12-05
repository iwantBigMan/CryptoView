package com.crypto.cryptoview.domain.usecase

import com.crypto.cryptoview.domain.model.UpbitAccountBalance
import com.crypto.cryptoview.domain.repository.UpbitAssetRepository

import javax.inject.Inject

class GetUpbitAccountBalancesUseCase @Inject constructor(
    private val repository: UpbitAssetRepository
) {
    suspend operator fun invoke(): Result<List<UpbitAccountBalance>> {
        return repository.getAccountBalances()
    }
}