package com.example.market.data.repo

import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.ProductResponse
import retrofit2.Response
import retrofit2.http.Query

interface Repository {
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getBrandProducts(vendor: String): Response<ProductResponse>
}