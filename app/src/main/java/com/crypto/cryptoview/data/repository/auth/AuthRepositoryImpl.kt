package com.crypto.cryptoview.data.repository.auth

import com.crypto.cryptoview.data.remote.api.AuthGateApi
import com.crypto.cryptoview.data.remote.api.AuthUpbitApi
import com.crypto.cryptoview.domain.repository.AuthRepository
import com.crypto.cryptoview.util.authHelper.GateIOAuthHelper
import com.crypto.cryptoview.util.authHelper.UpbitAuthHelper
import com.crypto.cryptoview.util.sha512Hex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authUpbitApi: AuthUpbitApi,
    private val authGateApi: AuthGateApi
) : AuthRepository {

    override suspend fun validateUpbit(apiKey: String, secretKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = UpbitAuthHelper.generateAuthToken(apiKey, secretKey, null)
                val resp = authUpbitApi.getAccounts(token)
                // 성공적으로 응답이 왔으면 true
                resp.isNotEmpty() || true
            } catch (t: Throwable) {
                android.util.Log.e("AuthRepository", "validateUpbit error", t)
                false
            }
        }
    }

    override suspend fun validateGate(apiKey: String, secretKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = (System.currentTimeMillis() / 1000).toString()
                val bodyHash = sha512Hex("")
                val signString = buildString {
                    append("GET")
                    append("\n")
                    append("/api/v4/spot/accounts")
                    append("\n")
                    append("")
                    append("\n")
                    append(bodyHash)
                    append("\n")
                    append(timestamp)
                }
                val sign = GateIOAuthHelper.generateSignature(secretKey, signString)
                val resp = authGateApi.getSpotAccounts(apiKey, timestamp, sign)
                resp.isNotEmpty() || true
            } catch (t: Throwable) {
                android.util.Log.e("AuthRepository", "validateGate error", t)
                false
            }
        }
    }
}
