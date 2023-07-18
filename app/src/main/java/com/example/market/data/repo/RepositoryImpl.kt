package com.example.market.data.repo

import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.remote.ApiService
import retrofit2.Response

class RepositoryImpl(private val apiService: ApiService) : Repository {
    override suspend fun getBrands(): Response<BrandResponse> {
        return apiService.getBrands()
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        return apiService.getProducts()
    }
}