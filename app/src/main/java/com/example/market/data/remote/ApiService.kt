package com.example.market.data.remote

import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.ProductResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("smart_collections.json")
    suspend fun getBrands(): Response<BrandResponse>

    @GET("products.json")
    suspend fun getProducts(): Response<ProductResponse>
}