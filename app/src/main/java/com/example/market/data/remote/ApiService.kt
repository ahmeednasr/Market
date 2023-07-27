package com.example.market.data.remote

import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.OrderResponse
import com.example.market.data.pojo.Currencies
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.pojo.*
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Query

interface ApiService {
    @GET("smart_collections.json")
    suspend fun getBrands(): Response<BrandResponse>

    @GET("products.json")
    suspend fun getProducts(): Response<ProductResponse>

    @GET("products.json")
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>

    @GET("products/{id}.json")
    suspend fun getSingleProduct(@Path("id") productId: Long): Response<ProductResponse>

    @POST("customers.json")
    suspend fun postCustomer(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json", @Body customer: NewUser
    ): Response<CustomerResponse>

    @PUT("customers/{id}.json")
    suspend fun updateCustomer(
        @Path("id") customerId: Long,
        @Body customer: Customer,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json"
    ): Response<CustomerResponse>

    @GET("customers/{id}/orders.json")
    suspend fun getCustomerOrders(@Path("id") userId: Long): Response<OrderResponse>

    @GET("currencies.json")
    suspend fun getCurrencies(): Response<Currencies>

    @GET("customers.json")
    suspend fun getAllCustomers(): Response<CustomersResponse>

    @GET("customers/{id}.json")
    suspend fun getSingleCustomer(@Path("id") customerID: Long): Response<CustomerResponse>

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

    @GET("draft_orders.json")
    suspend fun getDraftOrders(): Response<CartResponse>

    @PUT("draft_orders/{id}.json")
    suspend fun updateDraftOrder(
        @Path("id") id: Long,
        @Body order: DraftOrderResponse
    ): Response<DraftOrderResponse>

    //to getMax num of product
    @PUT("variants/{id}.json")
    suspend fun getVariantById(
        @Path("id") id: Long,
    ): Response<Variant>


    @DELETE("draft_orders/{id}.json")
    suspend fun deleteCartByID(@Path("id") id: Long?): Response<DraftOrderResponse>

    @PUT("draft_orders/{favouriteId}.json")
    suspend fun modifyFavourites(
        @Path("favouriteId") favouriteId: Long,
        @Body modifiedList: DraftOrderResponse
    )

    @GET("draft_orders/{favouriteId}.json")
    suspend fun getFavourites(@Path("favouriteId") favouriteId: Long): Response<DraftOrderResponse>

    @GET("/draft_orders/{id}.json")
    suspend fun getCartById(
        @Path("id") draftId: Long
    ): Response<DraftOrderResponse>

    @GET("price_rules.json")
    suspend fun getDiscountCodes(
    ): Response<DiscountResponse>

    @PUT("draft_orders/{cartId}.json")
    suspend fun modifyCart(
        @Path("cartId") cartId: Long,
        @Body modifiedList: DraftOrderResponse
    )

    @GET("draft_orders/{cartId}.json")
    suspend fun getCart(@Path("cartId") cartId: Long): Response<DraftOrderResponse>

    @DELETE("customers/{userID}/addresses/{address_id}.json")
    suspend fun deleteAddress(@Path("userID") userId: Long,@Path("address_id") addressId : Long)

    @PUT("customers/{userID}/addresses/{address_id}/default.json")
    suspend fun setDefault(@Path("userID") userId: Long,@Path("address_id") addressId : Long)

}