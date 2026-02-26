package com.josetoanto.subastas.features.pujas.data.datasources.remote.api

import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.CreatePujaRequestDto
import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.GanadorDto
import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.PujaDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PujasApi {

    @POST("api/v1/pujas")
    suspend fun createPuja(@Body body: CreatePujaRequestDto): PujaDto

    @GET("api/v1/pujas/producto/{producto_id}")
    suspend fun getPujasByProducto(@Path("producto_id") id: Int): List<PujaDto>

    @GET("api/v1/pujas/producto/{producto_id}/ganador")
    suspend fun getGanador(@Path("producto_id") id: Int): GanadorDto
}
