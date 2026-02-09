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