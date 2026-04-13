package com.josetoanto.subastas.core.di

import com.josetoanto.subastas.core.hardware.data.AndroidFeatureManager
import com.josetoanto.subastas.core.hardware.data.AndroidSoundManager
import com.josetoanto.subastas.core.hardware.data.AndroidVibrationManager
import com.josetoanto.subastas.core.hardware.domain.FeatureManager
import com.josetoanto.subastas.core.hardware.domain.SoundManager
import com.josetoanto.subastas.core.hardware.domain.VibrationManager
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

    @Binds
    @Singleton
    abstract fun bindSoundManager(impl: AndroidSoundManager): SoundManager

    @Binds
    @Singleton
    abstract fun bindVibrationManager(impl: AndroidVibrationManager): VibrationManager
}
