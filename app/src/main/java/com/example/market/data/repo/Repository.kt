package com.example.market.data.repo

import com.example.market.data.pojo.*
import retrofit2.Response
import retrofit2.http.Query

interface Repository {
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getBrandProducts(vendor: String): Response<ProductResponse>
    suspend fun getCurrencies(): Response<Currencies>
    suspend fun convertCurrency(from: String, to: String): Response<ConvertedCurrency>
    suspend fun getGovernment(country: String): Response<GovernmentPojo>
    suspend fun getCities(country: String, government: String): Response<CitiesPojo>
}