package com.josetoanto.subastas.features.productos.data.datasources.remote.api

import com.josetoanto.subastas.features.productos.data.datasources.remote.models.HfZeroShotRequest
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.HfZeroShotResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HuggingFaceApi {
    
    @POST("models/facebook/bart-large-mnli")
    suspend fun classifyText(
        @Header("Authorization") authorization: String? = null,
        @Body body: HfZeroShotRequest
    ): HfZeroShotResponse
}
