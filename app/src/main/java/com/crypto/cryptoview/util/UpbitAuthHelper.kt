package com.crypto.cryptoview.util


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.UUID

object UpbitAuthHelper {

    fun generateAuthToken(accessKey: String, secretKey: String): String {
        val algorithm = Algorithm.HMAC256(secretKey)
        val token = JWT.create()
            .withClaim("access_key", accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)

        return "Bearer $token"
    }
}