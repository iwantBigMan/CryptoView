package com.crypto.cryptoview.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.crypto.cryptoview.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google 로그인 서비스
 * Credential Manager API + Firebase Auth 조합
 * 거래소 API 키와는 완전히 분리된 사용자 인증 전용
 */
@Singleton
class GoogleAuthService @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    /** 현재 로그인된 Firebase 유저 */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /** 로그인 여부 */
    val isSignedIn: Boolean
        get() = firebaseAuth.currentUser != null

    /**
     * Google 로그인 실행
     * @param activityContext Activity Context (Credential Manager UI 표시에 필요)
     * @return 로그인된 FirebaseUser
     */
    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        return try {
            // 1. Google ID 옵션 생성
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build()

            // 2. Credential 요청
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // 3. Credential Manager로 로그인 UI 표시
            val result = credentialManager.getCredential(activityContext, request)

            // 4. Google ID Token 추출
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val idToken = googleIdTokenCredential.idToken

            // 5. Firebase Auth에 토큰 전달
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()

            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase 인증 실패: 사용자 정보를 가져올 수 없습니다"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("로그인이 취소되었습니다"))
        } catch (e: Exception) {
            android.util.Log.e("GoogleAuthService", "signInWithGoogle error", e)
            Result.failure(e)
        }
    }

    /**
     * 로그아웃 (Firebase + Credential Manager 모두)
     */
    suspend fun signOut() {
        try {
            // Credential Manager 세션 클리어
            credentialManager.clearCredentialState(
                androidx.credentials.ClearCredentialStateRequest()
            )
        } catch (e: Exception) {
            android.util.Log.e("GoogleAuthService", "clearCredentialState error", e)
        }
        // Firebase 로그아웃
        firebaseAuth.signOut()
    }

    /**
     * 현재 유저 정보
     */
    data class UserInfo(
        val uid: String,
        val displayName: String?,
        val email: String?,
        val photoUrl: String?
    )

    fun getUserInfo(): UserInfo? {
        val user = currentUser ?: return null
        return UserInfo(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString()
        )
    }
}

