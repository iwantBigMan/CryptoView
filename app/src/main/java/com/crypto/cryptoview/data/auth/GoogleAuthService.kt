package com.crypto.cryptoview.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.crypto.cryptoview.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    private val webClientId: String by lazy { context.getString(R.string.default_web_client_id) }

    /** 현재 로그인된 Firebase 유저 */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /** 로그인 여부 */
    val isSignedIn: Boolean
        get() = firebaseAuth.currentUser != null

    /**
     * Google 로그인 실행
     * 1차: GetGoogleIdOption (원탭 로그인)
     * 2차: GetSignInWithGoogleOption (Google 로그인 바텀시트 - fallback)
     */
    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        return try {
            // 1차 시도: 원탭 로그인 (기기에 이미 Google 계정이 있는 경우)
            val idToken = try {
                getIdTokenWithGoogleId(activityContext)
            } catch (e: NoCredentialException) {
                // 2차 시도: Google Sign-In 바텀시트 (계정 선택 UI 직접 표시)
                android.util.Log.d("GoogleAuthService", "원탭 로그인 실패, 바텀시트로 전환", e)
                getIdTokenWithSignInButton(activityContext)
            }

            // Firebase Auth에 토큰 전달
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
     * 방법 1: GetGoogleIdOption - 원탭 로그인
     * 기기에 Google 계정이 설정되어 있으면 바로 선택 가능
     */
    private suspend fun getIdTokenWithGoogleId(activityContext: Context): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(activityContext, request)
        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
        return credential.idToken
    }

    /**
     * 방법 2: GetSignInWithGoogleOption - Google 로그인 바텀시트
     * 원탭이 실패했을 때 fallback으로 사용
     * 기기에 Google 계정이 없거나 Credential Manager가 계정을 못 찾을 때
     */
    private suspend fun getIdTokenWithSignInButton(activityContext: Context): String {
        val signInOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        val result = credentialManager.getCredential(activityContext, request)
        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
        return credential.idToken
    }

      /**
       * 로그아웃 (Firebase + Credential Manager 모두)
       * 모든 저장된 Google 인증 정보를 완전히 제거 (SharedPreferences 캐시 포함)
       */
      suspend fun signOut() {
         android.util.Log.d("GoogleAuthService", "🔴 로그아웃 시작...")

         // 0단계: 현재 상태 로깅
         android.util.Log.d("GoogleAuthService", "로그아웃 전 - isSignedIn: $isSignedIn, currentUser: ${firebaseAuth.currentUser?.uid}")

         // 1단계: Firebase Authentication 세션 종료 (먼저 실행)
         try {
             withContext(Dispatchers.IO) {
                 firebaseAuth.signOut()
             }
             android.util.Log.d("GoogleAuthService", "✅ Firebase 로그아웃 완료")
         } catch (e: Exception) {
             android.util.Log.e("GoogleAuthService", "Firebase signOut 실패", e)
             throw e
         }

         // 2단계: Credential Manager에서 저장된 모든 자격증 제거
         try {
             credentialManager.clearCredentialState(
                 androidx.credentials.ClearCredentialStateRequest()
             )
             android.util.Log.d("GoogleAuthService", "✅ Credential Manager 상태 초기화 완료")
         } catch (e: Exception) {
             android.util.Log.w("GoogleAuthService", "⚠︎ Credential Manager 초기화 실패", e)
         }

         // 3단계: SharedPreferences 캐시 제거 (Google 토큰 캐시)
         try {
             val sharedPref = context.getSharedPreferences("com.google.android.gms.auth", Context.MODE_PRIVATE)
             sharedPref.edit().clear().apply()
             android.util.Log.d("GoogleAuthService", "✅ SharedPreferences 캐시 제거")
         } catch (e: Exception) {
             android.util.Log.w("GoogleAuthService", "⚠︎ SharedPreferences 제거 실패", e)
         }

         // 4단계: 로그아웃 상태 최종 검증
         android.util.Log.d("GoogleAuthService", "🔴 로그아웃 완료 검증")
         android.util.Log.d("GoogleAuthService", "로그아웃 후 - isSignedIn: $isSignedIn, currentUser: ${firebaseAuth.currentUser?.uid}")

         if (isSignedIn) {
             android.util.Log.e("GoogleAuthService", "❌ ERROR: 로그아웃 후에도 여전히 로그인됨! 강제 재시도...")
             withContext(Dispatchers.IO) {
                 firebaseAuth.signOut()
             }
             android.util.Log.d("GoogleAuthService", "❌ 강제 로그아웃 시도 후 - isSignedIn: $isSignedIn")

             if (isSignedIn) {
                 throw IllegalStateException("Firebase 로그아웃 후에도 로그인 상태가 유지됩니다")
             }
         }
      }

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
