package com.crypto.cryptoview.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.cryptoview.domain.usecase.auth.GetCurrentGoogleUserUseCase
import com.crypto.cryptoview.domain.usecase.auth.SignInWithGoogleUseCase
import com.crypto.cryptoview.domain.usecase.auth.SignOutGoogleUseCase
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
 * Domain UseCase만 의존 (클린 아키텍처)
 */
@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signOutGoogle: SignOutGoogleUseCase,
    private val getCurrentUser: GetCurrentGoogleUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoogleLoginUiState())
    val uiState: StateFlow<GoogleLoginUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = getCurrentUser()
        if (user != null) {
            _uiState.value = _uiState.value.copy(
                isSignedIn = true,
                userName = user.displayName,
                userEmail = user.email,
                userPhotoUrl = user.photoUrl
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

            signInWithGoogle.invoke(activityContext).fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignedIn = true,
                        userName = user.displayName,
                        userEmail = user.email,
                        userPhotoUrl = user.photoUrl
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

    fun signOut() {
        viewModelScope.launch {
            signOutGoogle()
            _uiState.value = GoogleLoginUiState()
        }
    }

    fun isSignedIn(): Boolean = getCurrentUser.isSignedIn()

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
