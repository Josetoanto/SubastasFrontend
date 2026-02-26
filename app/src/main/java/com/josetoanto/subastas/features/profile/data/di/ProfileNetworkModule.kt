package com.josetoanto.subastas.features.profile.data.di

import com.josetoanto.subastas.core.di.SubastasRetrofit
import com.josetoanto.subastas.features.profile.data.datasources.remote.api.ProfileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileNetworkModule {

    @Provides
    @Singleton
    fun provideProfileApi(@SubastasRetrofit retrofit: Retrofit): ProfileApi =
        retrofit.create(ProfileApi::class.java)
}
