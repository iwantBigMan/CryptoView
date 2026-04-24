package com.crypto.cryptoview.util.authHelper

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object GateIOAuthHelper {

    fun generateSignature(
        secretKey: String,
        message: String
    ): String {
        val algorithm = "HmacSHA512"
        val mac = Mac.getInstance(algorithm)
        val secretKeySpec =
            SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), algorithm)

        mac.init(secretKeySpec)

        val rawHmac = mac.doFinal(message.toByteArray(Charsets.UTF_8))

        // 🔥 Gate.io는 HEX 문자열
        return rawHmac.joinToString("") { "%02x".format(it) }
    }
}
