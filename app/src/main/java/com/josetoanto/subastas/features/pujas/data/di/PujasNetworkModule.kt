package com.josetoanto.subastas.features.pujas.data.di

import com.josetoanto.subastas.core.di.SubastasRetrofit
import com.josetoanto.subastas.features.pujas.data.datasources.remote.api.PujasApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PujasNetworkModule {

    @Provides
    @Singleton
    fun providePujasApi(@SubastasRetrofit retrofit: Retrofit): PujasApi =
        retrofit.create(PujasApi::class.java)
}
