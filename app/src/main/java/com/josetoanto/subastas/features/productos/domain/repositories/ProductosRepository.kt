package com.josetoanto.subastas.features.productos.domain.repositories

import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail

interface ProductosRepository {
    suspend fun getProductos(): Result<List<Producto>>
    suspend fun getProductoById(id: Int): Result<ProductoDetail>
    suspend fun createProducto(
        nombre: String,
        descripcion: String,
        precioInicial: Double,
        imagenUrl: String,
        fechaInicio: String,
        fechaFin: String
    ): Result<Producto>
    suspend fun updateProducto(
        id: Int,
        nombre: String?,
        descripcion: String?,
        imagenUrl: String?
    ): Result<Producto>
    suspend fun deleteProducto(id: Int): Result<Unit>
}
