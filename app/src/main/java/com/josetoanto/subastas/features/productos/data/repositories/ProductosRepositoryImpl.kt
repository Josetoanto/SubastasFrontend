package com.josetoanto.subastas.features.productos.data.repositories

import com.josetoanto.subastas.features.productos.data.datasources.remote.api.ProductosApi
import com.josetoanto.subastas.features.productos.data.datasources.remote.mapper.toDomain
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.CreateProductoRequestDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.UpdateProductoRequestDto
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductosRepositoryImpl @Inject constructor(
    private val api: ProductosApi
) : ProductosRepository {

    override suspend fun getProductos(): Result<List<Producto>> = runCatching {
        api.getProductos().map { it.toDomain() }
    }

    override suspend fun getProductoById(id: Int): Result<ProductoDetail> = runCatching {
        api.getProductoById(id).toDomain()
    }

    override suspend fun createProducto(
        nombre: String,
        descripcion: String,
        precioInicial: Double,
        imagenUrl: String,
        fechaInicio: String,
        fechaFin: String
    ): Result<Producto> = runCatching {
        val nombreBody = nombre.toRequestBody("text/plain".toMediaType())
        val descripcionBody = descripcion.toRequestBody("text/plain".toMediaType())
        val precioBody = precioInicial.toString().toRequestBody("text/plain".toMediaType())
        val fechaInicioBody = fechaInicio.toRequestBody("text/plain".toMediaType())
        val fechaFinBody = fechaFin.toRequestBody("text/plain".toMediaType())

        val imagenPart = if (imagenUrl.isNotBlank()) {
            val file = File(imagenUrl)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("imagen", file.name, requestFile)
        } else null

        api.createProducto(
            nombre = nombreBody,
            descripcion = descripcionBody,
            precioInicial = precioBody,
            fechaInicio = fechaInicioBody,
            fechaFin = fechaFinBody,
            imagen = imagenPart
        ).toDomain()
    }

    override suspend fun updateProducto(
        id: Int,
        nombre: String?,
        descripcion: String?,
        imagenUrl: String?
    ): Result<Producto> = runCatching {
        api.updateProducto(id, UpdateProductoRequestDto(nombre, descripcion, imagenUrl)).toDomain()
    }

    override suspend fun deleteProducto(id: Int): Result<Unit> = runCatching {
        api.deleteProducto(id)
    }
}
