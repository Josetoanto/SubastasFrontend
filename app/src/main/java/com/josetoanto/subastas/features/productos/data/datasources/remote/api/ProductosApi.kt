package com.josetoanto.subastas.features.productos.data.datasources.remote.api

import com.josetoanto.subastas.features.productos.data.datasources.remote.models.CreateProductoRequestDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.ProductoDetailDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.ProductoDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.UpdateProductoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductosApi {

    @GET("api/v1/productos")
    suspend fun getProductos(): List<ProductoDto>

    @POST("api/v1/productos")
    suspend fun createProducto(@Body body: CreateProductoRequestDto): ProductoDto

    @GET("api/v1/productos/{producto_id}")
    suspend fun getProductoById(@Path("producto_id") id: Int): ProductoDetailDto

    @PUT("api/v1/productos/{producto_id}")
    suspend fun updateProducto(
        @Path("producto_id") id: Int,
        @Body body: UpdateProductoRequestDto
    ): ProductoDto

    @DELETE("api/v1/productos/{producto_id}")
    suspend fun deleteProducto(@Path("producto_id") id: Int)
}
