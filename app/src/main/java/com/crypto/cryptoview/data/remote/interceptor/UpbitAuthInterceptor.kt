package com.crypto.cryptoview.data.remote.interceptor

import com.crypto.cryptoview.util.authHelper.UpbitAuthHelper
import com.crypto.cryptoview.data.local.CredentialsProvider
import okhttp3.Interceptor
import okhttp3.Response

class UpbitAuthInterceptor(
    private val credentialsProvider: CredentialsProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val creds = credentialsProvider.get()
        val accessKey = creds.upbitApiKey
        val secretKey = creds.upbitSecretKey

        // 키가 없거나 빈 값이면, 인증 헤더를 추가하지 않고 요청을 그대로 진행
        // API는 401을 반환할 것이며, UseCase에서 처리됨
        if (accessKey.isBlank() || secretKey.isBlank()) {
            return chain.proceed(original)
        }

        val queryString = original.url.query
        val authToken = UpbitAuthHelper.generateAuthToken(
            accessKey = accessKey,
            secretKey = secretKey,
            queryOrBody = queryString
        )

        val request = original.newBuilder()
            .header("Authorization", authToken)
            .build()

        return chain.proceed(request)
    }
}