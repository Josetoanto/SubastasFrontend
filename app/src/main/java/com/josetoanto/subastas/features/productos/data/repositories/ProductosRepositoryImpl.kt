package com.josetoanto.subastas.features.productos.data.repositories

import com.josetoanto.subastas.features.productos.data.datasources.remote.api.ProductosApi
import com.josetoanto.subastas.features.productos.data.datasources.remote.mapper.toDomain
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.CreateProductoRequestDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.UpdateProductoRequestDto
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

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
        api.createProducto(
            CreateProductoRequestDto(
                nombre = nombre,
                descripcion = descripcion,
                precioInicial = precioInicial,
                imagenUrl = imagenUrl,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )
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
