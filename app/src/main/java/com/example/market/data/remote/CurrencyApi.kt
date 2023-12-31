package com.example.market.data.remote

import com.example.market.data.pojo.ConvertedCurrency
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("convert")
    suspend fun convertCurrency(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: String,
    ): Response<ConvertedCurrency>
}