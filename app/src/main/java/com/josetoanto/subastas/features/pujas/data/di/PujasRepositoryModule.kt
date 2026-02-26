package com.josetoanto.subastas.features.pujas.data.di

import com.josetoanto.subastas.features.pujas.data.repositories.PujasRepositoryImpl
import com.josetoanto.subastas.features.pujas.domain.repositories.PujasRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PujasRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPujasRepository(impl: PujasRepositoryImpl): PujasRepository
}
