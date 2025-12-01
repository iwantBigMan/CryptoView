package com.crypto.cryptoview.net

import com.crypto.cryptoview.model.AccountBalance
import retrofit2.http.GET
import retrofit2.http.Header


interface Api {

    @GET("v1/accounts")
    suspend fun getAccountBalances(
        @Header("Authorization") authorization: String
    ): List<AccountBalance>
}