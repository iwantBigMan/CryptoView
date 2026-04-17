package com.crypto.cryptoview.domain.repository

import com.crypto.cryptoview.domain.model.GoogleUser

/**
 * Google 인증 Repository 인터페이스 (Domain Layer)
 * data 레이어의 Firebase/Credential Manager 구현을 추상화
 */
interface GoogleAuthRepository {
    val isSignedIn: Boolean
    fun getCurrentUser(): GoogleUser?
    suspend fun signIn(activityContext: Any): Result<GoogleUser>
    suspend fun signOut()
}

