package com.crypto.cryptoview.data.repository.auth

import android.content.Context
import com.crypto.cryptoview.data.auth.GoogleAuthService
import com.crypto.cryptoview.domain.model.GoogleUser
import com.crypto.cryptoview.domain.repository.GoogleAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GoogleAuthRepository 구현체 (Data Layer)
 * GoogleAuthService를 래핑하여 Domain 모델로 변환
 */
@Singleton
class GoogleAuthRepositoryImpl @Inject constructor(
    private val googleAuthService: GoogleAuthService
) : GoogleAuthRepository {

    override val isSignedIn: Boolean
        get() = googleAuthService.isSignedIn

    override fun getCurrentUser(): GoogleUser? {
        return googleAuthService.getUserInfo()?.let {
            GoogleUser(
                uid = it.uid,
                displayName = it.displayName,
                email = it.email,
                photoUrl = it.photoUrl
            )
        }
    }

    override suspend fun signIn(activityContext: Any): Result<GoogleUser> {
        require(activityContext is Context) { "activityContext must be Android Context" }
        return googleAuthService.signInWithGoogle(activityContext).map { firebaseUser ->
            GoogleUser(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
        }
    }

    override suspend fun signOut() {
        googleAuthService.signOut()
    }
}

