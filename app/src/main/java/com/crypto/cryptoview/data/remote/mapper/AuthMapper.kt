package com.crypto.cryptoview.data.remote.mapper

import com.crypto.cryptoview.data.remote.dto.upbit.DeleteUpbitCredentialResponse
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import com.crypto.cryptoview.domain.model.auth.CredentialDeletionResult
import com.crypto.cryptoview.domain.model.auth.UpbitCredentialValidationResult

fun ValidateUpbitResponse.toDomain(): UpbitCredentialValidationResult {
    return UpbitCredentialValidationResult(
        valid = valid,
        message = message,
        saved = saved == true
    )
}

fun DeleteUpbitCredentialResponse.toDomain(): CredentialDeletionResult {
    return CredentialDeletionResult(
        deleted = deleted,
        message = message
    )
}
