package com.josetoanto.subastas.features.productos.data.datasources.remote.api

import com.josetoanto.subastas.features.productos.data.datasources.remote.models.AnalyticsDtos
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.ProductoDetailDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.ProductoDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.UpdateProductoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ProductosApi {

    @GET("api/v1/productos")
    suspend fun getProductos(
        @Query("ciudad") ciudad: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("radio_km") radioKm: Double? = null,
        @Query("solo_relampago") soloRelampago: Boolean? = null,
        @Query("solo_entrega_persona") soloEntregaPersona: Boolean? = null
    ): List<ProductoDto>

    @Multipart
    @POST("api/v1/productos")
    suspend fun createProducto(
        @Part("nombre") nombre: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("precio_inicial") precioInicial: RequestBody,
        @Part("fecha_inicio") fechaInicio: RequestBody,
        @Part("fecha_fin") fechaFin: RequestBody,
        @Part("latitud") latitud: RequestBody? = null,
        @Part("longitud") longitud: RequestBody? = null,
        @Part("ciudad") ciudad: RequestBody? = null,
        @Part("entrega_en_persona") entregaEnPersona: RequestBody? = null,
        @Part("es_relampago") esRelampago: RequestBody? = null,
        @Part imagen: MultipartBody.Part? = null
    ): ProductoDto

    @GET("api/v1/productos/{producto_id}")
    suspend fun getProductoById(@Path("producto_id") id: Int): ProductoDetailDto

    @GET("api/v1/productos/{producto_id}/analytics")
    suspend fun getProductoAnalytics(@Path("producto_id") id: Int): AnalyticsDtos.ProductoAnalyticsDto

    @PUT("api/v1/productos/{producto_id}")
    suspend fun updateProducto(
        @Path("producto_id") id: Int,
        @Body body: UpdateProductoRequestDto
    ): ProductoDto

    @DELETE("api/v1/productos/{producto_id}")
    suspend fun deleteProducto(@Path("producto_id") id: Int)
}
