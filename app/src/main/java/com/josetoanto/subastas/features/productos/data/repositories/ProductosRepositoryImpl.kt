package com.josetoanto.subastas.features.productos.data.repositories

import com.josetoanto.subastas.features.productos.data.datasources.remote.api.ProductosApi
import com.josetoanto.subastas.features.productos.data.datasources.remote.mapper.*
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.UpdateProductoRequestDto
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.entities.ProductoAnalytics
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

    override suspend fun getProductos(
        ciudad: String?,
        lat: Double?,
        lon: Double?,
        radioKm: Double?,
        soloRelampago: Boolean,
        soloEntregaPersona: Boolean
    ): Result<List<Producto>> = runCatching {
        api.getProductos(
            ciudad = ciudad,
            lat = lat,
            lon = lon,
            radioKm = radioKm,
            soloRelampago = if (soloRelampago) true else null,
            soloEntregaPersona = if (soloEntregaPersona) true else null
        ).map { it.toDomain() }
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
        fechaFin: String,
        latitud: Double?,
        longitud: Double?,
        ciudad: String?,
        entregaEnPersona: Boolean,
        esRelampago: Boolean
    ): Result<Producto> = runCatching {
        val plain = "text/plain".toMediaType()
        val nombreBody = nombre.toRequestBody(plain)
        val descripcionBody = descripcion.toRequestBody(plain)
        val precioBody = precioInicial.toString().toRequestBody(plain)
        val fechaInicioBody = fechaInicio.toRequestBody(plain)
        val fechaFinBody = fechaFin.toRequestBody(plain)
        val latitudBody = latitud?.toString()?.toRequestBody(plain)
        val longitudBody = longitud?.toString()?.toRequestBody(plain)
        val ciudadBody = ciudad?.toRequestBody(plain)
        val entregaBody = entregaEnPersona.toString().toRequestBody(plain)
        val relampagoBody = esRelampago.toString().toRequestBody(plain)

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
            latitud = latitudBody,
            longitud = longitudBody,
            ciudad = ciudadBody,
            entregaEnPersona = entregaBody,
            esRelampago = relampagoBody,
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

    override suspend fun getProductoAnalytics(id: Int): Result<ProductoAnalytics> = runCatching {
        api.getProductoAnalytics(id).toDomain()
    }
}
