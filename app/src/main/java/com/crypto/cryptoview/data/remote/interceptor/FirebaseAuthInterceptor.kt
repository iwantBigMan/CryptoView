package com.crypto.cryptoview.data.remote.interceptor

import com.crypto.cryptoview.data.auth.FirebaseTokenProviderImpl
import okhttp3.Interceptor
import okhttp3.Response

class FirebaseAuthInterceptor(
    private val tokenProvider: FirebaseTokenProviderImpl
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider.getIdTokenBlocking()

        if (token.isNullOrBlank()) {
            return chain.proceed(original)
        }

        val request = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}