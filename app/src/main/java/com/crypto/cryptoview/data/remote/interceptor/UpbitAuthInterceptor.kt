package com.crypto.cryptoview.data.remote.interceptor

import com.crypto.cryptoview.util.UpbitAuthHelper
import okhttp3.Interceptor
import okhttp3.Response

class UpbitAuthInterceptor(
    private val accessKey: String,
    private val secretKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

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