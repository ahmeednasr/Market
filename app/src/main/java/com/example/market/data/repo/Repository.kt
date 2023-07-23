package com.example.market.data.repo

import com.example.market.data.pojo.*
import retrofit2.Response

interface Repository {
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getBrandProducts(vendor: String): Response<ProductResponse>
    suspend fun getCurrencies(): Response<Currencies>
    suspend fun convertCurrency(from: String, to: String, amount: Double): Response<ConvertedCurrency>
    suspend fun createUser(user: NewUser): Response<CustomerResponse>
    suspend fun getAllCustomers(): Response<CustomersResponse>
    suspend fun createFavouriteDraftOrder(favouriteDraftOrder: DraftOrderResponse): Response<DraftOrderResponse>
    suspend fun createCartDraftOrder(cartDraftOrder: DraftOrderResponse): Response<DraftOrderResponse>
    suspend fun getGovernment(country: String): Response<GovernmentPojo>
    suspend fun getCities(country: String, government: String): Response<CitiesPojo>
    suspend fun getDiscountCodes(): Response<DiscountResponse>
    suspend fun getDraftOrders():Response<CartResponse>

}