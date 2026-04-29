package com.crypto.cryptoview.data.remote.mapper

import com.crypto.cryptoview.data.remote.dto.gateio.GateIoCredentialDeleteResponse
import com.crypto.cryptoview.data.remote.dto.gateio.GateIoValidateAndSaveResponse
import com.crypto.cryptoview.data.remote.dto.upbit.DeleteUpbitCredentialResponse
import com.crypto.cryptoview.data.remote.dto.upbit.ValidateUpbitResponse
import com.crypto.cryptoview.domain.model.auth.CredentialDeletionResult
import com.crypto.cryptoview.domain.model.auth.CredentialValidationResult

fun ValidateUpbitResponse.toDomain(): CredentialValidationResult {
    return CredentialValidationResult(
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

fun GateIoValidateAndSaveResponse.toDomain(): CredentialValidationResult {
    return CredentialValidationResult(
        valid = valid,
        message = message,
        saved = saved == true
    )
}

fun GateIoCredentialDeleteResponse.toDomain(): CredentialDeletionResult {
    return CredentialDeletionResult(
        deleted = deleted,
        message = message
    )
}
