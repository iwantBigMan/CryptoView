package com.crypto.cryptoview.domain.usecase.auth

import com.crypto.cryptoview.domain.model.auth.GoogleUser
import com.crypto.cryptoview.domain.repository.GoogleAuthRepository
import javax.inject.Inject

/**
 * Google 로그인 실행 UseCase
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val repository: GoogleAuthRepository
) {
    suspend operator fun invoke(activityContext: Any): Result<GoogleUser> {
        return repository.signIn(activityContext)
    }
}

