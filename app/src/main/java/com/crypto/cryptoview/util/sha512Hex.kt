package com.crypto.cryptoview.util

import java.security.MessageDigest

fun sha512Hex(input: String): String {
    val md = MessageDigest.getInstance("SHA-512")
    val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}