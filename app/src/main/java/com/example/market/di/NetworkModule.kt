package com.example.market.di

import com.example.market.data.remote.ApiService
import com.example.market.utils.Constants.API_ACCESS_TOKEN
import com.example.market.utils.Constants.API_KEY
import com.example.market.utils.Constants.BASE_URL
import com.example.market.utils.Constants.NETWORK_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHeadersInterceptor() =
        Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .build()
            )
        }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Provides
    @Singleton
    fun authInterceptor(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(API_KEY, API_ACCESS_TOKEN)
        return chain.proceed(
            chain.request().newBuilder().header("Authorization", credentials)
                .build()
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headersInterceptor: Interceptor,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(headersInterceptor)
            .addInterceptor(logging)
            .addInterceptor(::authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}