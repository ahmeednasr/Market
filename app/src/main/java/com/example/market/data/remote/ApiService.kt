package com.example.market.data.remote

import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.OrderResponse
import com.example.market.data.pojo.Currencies
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.pojo.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("smart_collections.json")
    suspend fun getBrands(): Response<BrandResponse>

    @GET("products.json")
    suspend fun getProducts(): Response<ProductResponse>

    @GET("products.json")
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>

    @POST("customers.json")
    suspend fun postCustomer(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json", @Body customer: NewUser
    ): Response<CustomerResponse>

    @GET("customers/{id}/orders.json")
    suspend fun getCustomerOrders(@Path("id") userId: Long): Response<OrderResponse>

    @GET("currencies.json")
    suspend fun getCurrencies(): Response<Currencies>

    @GET("customers.json")
    suspend fun getAllCustomers(): Response<CustomersResponse>

    @POST("draft_orders.json")
    suspend fun createFavouriteDraftOrder(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
        @Body favouriteDraftOrder: DraftOrderResponse
    ): Response<DraftOrderResponse>

    @POST("draft_orders.json")
    suspend fun createCartDraftOrder(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
        @Body cartDraftOrder: DraftOrderResponse
    ): Response<DraftOrderResponse>

    @GET("/draft_orders/{id}.json")
    suspend fun getDraftById(
        @Path("id") draftId: Long
    ): Response<DraftOrderResponse>

    @GET("price_rules/1401023660351/discount_codes.json")
    suspend fun getDiscountCodes(
    ): Response<DiscountResponse>

}