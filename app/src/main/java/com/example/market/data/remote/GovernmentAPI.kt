package com.example.market.data.remote

import com.example.market.data.pojo.CitiesPojo
import com.example.market.data.pojo.CitiesRequest
import com.example.market.data.pojo.Country
import com.example.market.data.pojo.GovernmentPojo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GovernmentAPI {
    @POST("countries/states")
    suspend fun getGovernment(
        @Body country: Country,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json"
    ): Response<GovernmentPojo>

    @POST("countries/state/cities")
    suspend fun getCities(
        @Body requestBody: CitiesRequest,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json"
    ): Response<CitiesPojo>

}

