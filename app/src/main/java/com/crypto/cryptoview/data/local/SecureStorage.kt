package com.crypto.cryptoview.data.local

import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android Keystore 기반 AES/GCM 암복호화 유틸
 * - keyAlias로 AndroidKeyStore에 대칭키를 생성/조회
 * - 암호화된 데이터는 Base64(iv + ciphertext)로 저장
 */
object SecureStorage {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "crypto_view_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG = "SecureStorage"

    private fun getKey(): SecretKey? {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(KEY_ALIAS)) {
                val entry = keyStore.getKey(KEY_ALIAS, null)
                if (entry is SecretKey) return entry
            }

            // Key 생성 (AndroidKeyStore 지원 가정)
            val keyGenerator = KeyGenerator.getInstance("AES", ANDROID_KEYSTORE)
            val spec = android.security.keystore.KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(spec)
            return keyGenerator.generateKey()
        } catch (t: Throwable) {
            Log.e(TAG, "getKey error", t)
        }
        return null
    }

    fun encrypt(plain: String): String? {
        try {
            val key = getKey() ?: return null
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv // 12 bytes recommended
            val ciphertext = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
            val combined = ByteArray(iv.size + ciphertext.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(ciphertext, 0, combined, iv.size, ciphertext.size)
            return Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (t: Throwable) {
            Log.e(TAG, "encrypt error", t)
            return null
        }
    }

    fun decrypt(encoded: String): String? {
        try {
            val key = getKey() ?: return null
            val combined = Base64.decode(encoded, Base64.NO_WRAP)
            if (combined.size < 13) return null
            val iv = combined.copyOfRange(0, 12)
            val ciphertext = combined.copyOfRange(12, combined.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val plain = cipher.doFinal(ciphertext)
            return String(plain, Charsets.UTF_8)
        } catch (t: Throwable) {
            Log.e(TAG, "decrypt error", t)
            return null
        }
    }
}
