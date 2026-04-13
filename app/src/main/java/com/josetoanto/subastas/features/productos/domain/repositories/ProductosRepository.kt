package com.josetoanto.subastas.features.productos.domain.repositories

import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.entities.ProductoAnalytics
import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail

interface ProductosRepository {
    suspend fun getProductos(
        ciudad: String? = null,
        lat: Double? = null,
        lon: Double? = null,
        radioKm: Double? = null,
        soloRelampago: Boolean = false,
        soloEntregaPersona: Boolean = false
    ): Result<List<Producto>>

    suspend fun getProductoById(id: Int): Result<ProductoDetail>

    suspend fun createProducto(
        nombre: String,
        descripcion: String,
        precioInicial: Double,
        imagenUrl: String,
        fechaInicio: String,
        fechaFin: String,
        latitud: Double? = null,
        longitud: Double? = null,
        ciudad: String? = null,
        entregaEnPersona: Boolean = false,
        esRelampago: Boolean = false
    ): Result<Producto>

    suspend fun updateProducto(
        id: Int,
        nombre: String?,
        descripcion: String?,
        imagenUrl: String?
    ): Result<Producto>

    suspend fun deleteProducto(id: Int): Result<Unit>

    suspend fun getProductoAnalytics(id: Int): Result<ProductoAnalytics>
}
