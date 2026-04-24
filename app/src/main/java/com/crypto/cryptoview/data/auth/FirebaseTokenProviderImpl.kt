package com.crypto.cryptoview.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTokenProviderImpl @Inject constructor() : FirebaseTokenProvider {

    override suspend fun getIdToken(): String? {
        return try {
            FirebaseAuth.getInstance()
                .currentUser
                ?.getIdToken(false)
                ?.await()
                ?.token
        } catch (e: Exception) {
            null
        }
    }

    /** 인터셉터(비-suspend 환경)에서 사용하는 동기 버전 */
    fun getIdTokenBlocking(): String? = runBlocking { getIdToken() }
}

