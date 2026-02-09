package com.crypto.cryptoview.data.remote.interceptor


import com.crypto.cryptoview.util.authHelper.GateIOAuthHelper
import com.crypto.cryptoview.util.sha512Hex
import com.crypto.cryptoview.data.local.CredentialsProvider
import okhttp3.Interceptor
import okhttp3.Response

class GateIOAuthInterceptor(
    private val credentialsProvider: CredentialsProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val creds = credentialsProvider.get()
        val apiKey = creds.gateioApiKey
        val secretKey = creds.gateioSecretKey

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val method = request.method
        val path = request.url.encodedPath
        val query = request.url.encodedQuery ?: ""

        val bodyString = request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        } ?: ""

        val bodyHash = sha512Hex(bodyString)

        val signString = buildString {
            append(method)
            append("\n")
            append(path)
            append("\n")
            append(query)
            append("\n")
            append(bodyHash)
            append("\n")
            append(timestamp)
        }

        val sign = GateIOAuthHelper.generateSignature(secretKey, signString)

        val newRequest = request.newBuilder()
            .addHeader("KEY", apiKey)
            .addHeader("Timestamp", timestamp)
            .addHeader("SIGN", sign)
            .build()

        return chain.proceed(newRequest)
    }
}
