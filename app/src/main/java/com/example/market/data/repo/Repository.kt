package com.example.market.data.repo

import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.ProductResponse
import retrofit2.Response

interface Repository {
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getProducts(): Response<ProductResponse>
}