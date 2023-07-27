package com.example.market.data.repo

import com.example.market.data.pojo.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface Repository {
    suspend fun getBrands(): Response<BrandResponse>
    suspend fun getProducts(): Response<ProductResponse>
    suspend fun getSingleProduct(productId: Long): Response<ProductResponse>
    suspend fun getBrandProducts(vendor: String): Response<ProductResponse>
    suspend fun getCurrencies(): Response<Currencies>
    suspend fun convertCurrency(from: String, to: String, amount: Double): Response<ConvertedCurrency>
    suspend fun createUser(user: NewUser): Response<CustomerResponse>
    suspend fun getAllCustomers(): Response<CustomersResponse>
    suspend fun getCustomer(customerID : Long): Response<CustomerResponse>
    suspend fun getFavourites(favouriteId: Long): Response<DraftOrderResponse>
    suspend fun getGovernment(country: String): Response<GovernmentPojo>
    suspend fun getCities(country: String, government: String): Response<CitiesPojo>
    suspend fun getDiscountCodes(): Response<DiscountResponse>
    suspend fun getCustomerOrders(userId: Long): Response<OrderResponse>
    suspend fun getSingleOrder(orderId: Long): Response<OrderResponse>
    suspend fun getDraftOrders(): Response<CartResponse>
    suspend fun deleteCartByID(id: Long): Response<DraftOrderResponse>
    suspend fun getCart(cartId: Long): Response<DraftOrderResponse>
    suspend fun modifyCart(cartId: Long, modifiedList: DraftOrderResponse)
    suspend fun addAddressToUser(userId: Long,address: CustomerResponse):Response<CustomerResponse>
    suspend fun getVariant(id: Long): Response<VariantResponse>
    suspend fun createFavouriteDraftOrder(favouriteDraftOrder: DraftOrderResponse): Response<DraftOrderResponse>
    suspend fun createCartDraftOrder(cartDraftOrder: DraftOrderResponse): Response<DraftOrderResponse>
    suspend fun modifyFavourites(favouriteId: Long, modifiedList: DraftOrderResponse)
}