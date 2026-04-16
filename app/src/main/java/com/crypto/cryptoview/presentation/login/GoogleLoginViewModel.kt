package com.crypto.cryptoview.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.data.auth.GoogleAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Google 로그인 UI 상태
 */
data class GoogleLoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhotoUrl: String? = null,
    val error: String? = null
)

/**
 * Google 로그인 ViewModel
 * Firebase Auth + Credential Manager 기반
 */
@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val googleAuthService: GoogleAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoogleLoginUiState())
    val uiState: StateFlow<GoogleLoginUiState> = _uiState.asStateFlow()

    init {
        // 앱 시작 시 이미 로그인된 상태인지 확인
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val userInfo = googleAuthService.getUserInfo()
        if (userInfo != null) {
            _uiState.value = _uiState.value.copy(
                isSignedIn = true,
                userName = userInfo.displayName,
                userEmail = userInfo.email,
                userPhotoUrl = userInfo.photoUrl
            )
        }
    }

    /**
     * Google 로그인 실행
     * @param activityContext Activity context (Credential Manager UI에 필요)
     */
    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            googleAuthService.signInWithGoogle(activityContext).fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignedIn = true,
                        userName = user.displayName,
                        userEmail = user.email,
                        userPhotoUrl = user.photoUrl?.toString()
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "로그인 실패"
                    )
                }
            )
        }
    }

    /**
     * 로그아웃 (Firebase + Google)
     */
    fun signOut() {
        viewModelScope.launch {
            googleAuthService.signOut()
            _uiState.value = GoogleLoginUiState() // 상태 초기화
        }
    }

    /** Firebase 로그인 여부 (suspend 없이) */
    fun isSignedIn(): Boolean = googleAuthService.isSignedIn

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

