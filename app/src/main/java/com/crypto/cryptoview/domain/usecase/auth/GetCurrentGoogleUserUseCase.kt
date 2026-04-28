package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.model.auth.GoogleUser
import com.crypto.cryptoview.domain.repository.GoogleAuthRepository
import javax.inject.Inject

/**
 * 현재 로그인된 Google 사용자 조회 UseCase
 */
class GetCurrentGoogleUserUseCase @Inject constructor(
    private val repository: GoogleAuthRepository
) {
    operator fun invoke(): GoogleUser? = repository.getCurrentUser()

    fun isSignedIn(): Boolean = repository.isSignedIn
}

