package com.example.market.data.remote

import com.example.market.data.pojo.ConvertedCurrency
import retrofit2.Response

interface CurrencyApi {
    suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Int = 1
    ): Response<ConvertedCurrency>

}