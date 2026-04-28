package com.crypto.cryptoview.domain.model.auth

/**
 * Google 로그인 사용자 정보 (Domain Model)
 * Firebase/Credential Manager 등 외부 의존성 없음
 */
data class GoogleUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

