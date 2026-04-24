package com.crypto.cryptoview.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * /account 경로 요청의 응답 바디만 Logcat에 출력하는 디버그 인터셉터
 * TAG: ACCOUNT_RESPONSE
 */
class AccountResponseLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.url.encodedPath.contains("account", ignoreCase = true)) {
            val bodyString = response.peekBody(Long.MAX_VALUE).string()
            Log.d(TAG, "URL  : ${request.url}")
            Log.d(TAG, "CODE : ${response.code}")
            Log.d(TAG, "BODY : $bodyString")
        }

        return response
    }

    companion object {
        const val TAG = "ACCOUNT_RESPONSE"
    }
}

