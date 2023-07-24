package com.example.market.data.repo

import com.example.market.data.pojo.*
import com.example.market.data.remote.ApiService
import com.example.market.data.remote.CurrencyApi
import com.example.market.data.remote.GovernmentAPI
import retrofit2.Response

class RepositoryImpl(
    private val apiService: ApiService,
    private val currencyApi: CurrencyApi,
    private val governmentAPI: GovernmentAPI
) : Repository {
    override suspend fun getBrands(): Response<BrandResponse> {
        return apiService.getBrands()
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        return apiService.getProducts()
    }

    override suspend fun getSingleProduct(productId: Long): Response<ProductResponse> {
        return apiService.getSingleProduct(productId)
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        return apiService.getBrandProducts(vendor)
    }

    override suspend fun getCurrencies(): Response<Currencies> {
        return apiService.getCurrencies()
    }

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double
    ): Response<ConvertedCurrency> {
        return currencyApi.convertCurrency(from, to, amount.toString())
    }

    override suspend fun createUser(user: NewUser): Response<CustomerResponse> {
        return apiService.postCustomer(customer = user)
    }

    override suspend fun getAllCustomers(): Response<CustomersResponse> {
        return apiService.getAllCustomers()
    }

    override suspend fun createFavouriteDraftOrder(favouriteDraftOrder: DraftOrderResponse): Response<DraftOrderResponse> {
        return apiService.createFavouriteDraftOrder(favouriteDraftOrder = favouriteDraftOrder)
    }

    override suspend fun createCartDraftOrder(cartDraftOrder: DraftOrderResponse): Response<DraftOrderResponse> {
        return apiService.createCartDraftOrder(cartDraftOrder = cartDraftOrder)
    }

    override suspend fun modifyFavourites(favouriteId: Long, modifiedList: DraftOrderResponse) {
        apiService.modifyFavourites(favouriteId, modifiedList)
    }

    override suspend fun getFavourites(favouriteId: Long): Response<DraftOrderResponse> {
        return apiService.getFavourites(favouriteId)
    }


    override suspend fun getGovernment(country: String): Response<GovernmentPojo> {
        val countryObj = Country(country)
        return governmentAPI.getGovernment(countryObj)
    }

    override suspend fun getCities(country: String, government: String): Response<CitiesPojo> {
        val citiesRequest = CitiesRequest(country, government)
        return governmentAPI.getCities(citiesRequest)
    }

    override suspend fun getDiscountCodes(): Response<DiscountResponse> {
        return apiService.getDiscountCodes()
    }

    override suspend fun getDraftOrders(): Response<CartResponse> {
        return apiService.getDraftOrders()
    }
}