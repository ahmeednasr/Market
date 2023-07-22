package com.example.market.di

import android.content.Context
import android.content.SharedPreferences
import com.example.market.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun getSharedPreferences(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences(Constants.SharedPreferences  ,Context.MODE_PRIVATE)
    }
}