package com.josetoanto.subastas.core.di

import com.josetoanto.subastas.features.productos.data.datasources.remote.api.HuggingFaceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HuggingFaceRetrofit

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    @HuggingFaceRetrofit
    fun provideHuggingFaceRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        val jsonParser = Json { ignoreUnknownKeys = true }
        
        // Basic OkHttpClient for AI Requests, longer timeouts due to Model cold starts
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .client(okHttpClient)
            .addConverterFactory(jsonParser.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideHuggingFaceApi(
        @HuggingFaceRetrofit retrofit: Retrofit
    ): HuggingFaceApi {
        return retrofit.create(HuggingFaceApi::class.java)
    }
}
