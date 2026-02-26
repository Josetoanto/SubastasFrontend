package com.josetoanto.subastas.features.productos.data.di

import com.josetoanto.subastas.core.di.SubastasRetrofit
import com.josetoanto.subastas.features.productos.data.datasources.remote.api.ProductosApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductosNetworkModule {

    @Provides
    @Singleton
    fun provideProductosApi(@SubastasRetrofit retrofit: Retrofit): ProductosApi =
        retrofit.create(ProductosApi::class.java)
}
