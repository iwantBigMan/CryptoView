package com.crypto.cryptoview.data.auth

interface FirebaseTokenProvider {
    suspend fun getIdToken(): String?
}