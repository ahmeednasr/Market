package com.example.market.data.repo

import com.example.market.data.pojo.*
import com.example.market.data.remote.ApiService
import com.example.market.data.remote.CurrencyApi
import retrofit2.Response

class RepositoryImpl(
    private val apiService: ApiService,
    private val currencyApi: CurrencyApi
) : Repository {
    override suspend fun getBrands(): Response<BrandResponse> {
        return apiService.getBrands()
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        return apiService.getProducts()
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        return apiService.getBrandProducts(vendor)
    }

    override suspend fun getCurrencies(): Response<Currencies> {
        return apiService.getCurrencies()
    }

    override suspend fun convertCurrency(from: String, to: String): Response<ConvertedCurrency> {
        return currencyApi.convertCurrency(from, to)
    }

    override suspend fun createUser(user: NewUser): Response<CustomerResponse> {
        return apiService.postCustomer(customer = user)
    }

    override suspend fun getAllCustomers(): Response<CustomersResponse> {
        return apiService.getAllCustomers()
    }


}