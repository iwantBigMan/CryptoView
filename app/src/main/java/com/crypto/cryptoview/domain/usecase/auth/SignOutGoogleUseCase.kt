package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.repository.GoogleAuthRepository
import javax.inject.Inject

/**
 * Google 로그아웃 UseCase
 */
class SignOutGoogleUseCase @Inject constructor(
    private val repository: GoogleAuthRepository
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}

