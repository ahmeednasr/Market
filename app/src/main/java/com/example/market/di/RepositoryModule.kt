package com.example.market.di

import com.example.market.data.remote.ApiService
import com.example.market.data.remote.CurrencyApi
import com.example.market.data.repo.Repository
import com.example.market.data.repo.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMoviesRepository(
        apiService: ApiService,
        currencyApi: CurrencyApi,
    ): Repository {
        return RepositoryImpl(apiService, currencyApi)
    }
}