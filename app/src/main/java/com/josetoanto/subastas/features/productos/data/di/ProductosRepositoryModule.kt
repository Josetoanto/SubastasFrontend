package com.josetoanto.subastas.features.productos.data.di

import com.josetoanto.subastas.features.productos.data.repositories.ProductosRepositoryImpl
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductosRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductosRepository(impl: ProductosRepositoryImpl): ProductosRepository
}
