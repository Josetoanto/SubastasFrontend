package com.josetoanto.subastas.core.di

import com.josetoanto.subastas.core.hardware.data.AndroidFeatureManager
import com.josetoanto.subastas.core.hardware.domain.FeatureManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindFeatureManager(impl: AndroidFeatureManager): FeatureManager
}
