// language: kotlin
package com.crypto.cryptoview.util.authHelper

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.MessageDigest
import java.util.UUID

object UpbitAuthHelper {

    private fun sha512Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * queryOrBody: 쿼리 문자열(예: "market=KRW-BTC") 또는 POST 바디 원문.
     * 비어있으면 query_hash 클레임은 추가하지 않음.
     */
    fun generateAuthToken(accessKey: String, secretKey: String, queryOrBody: String? = null): String {
        val algorithm = Algorithm.HMAC256(secretKey)
        val builder = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())

        if (!queryOrBody.isNullOrEmpty()) {
            builder.withClaim("query_hash", sha512Hex(queryOrBody))
            builder.withClaim("query_hash_alg", "SHA512")
        }

        val token = builder.sign(algorithm)
        return "Bearer $token"
    }
}