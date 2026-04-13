package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class CreateProductoUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(
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
    ): Result<Producto> = repository.createProducto(
        nombre, descripcion, precioInicial, imagenUrl, fechaInicio, fechaFin,
        latitud, longitud, ciudad, entregaEnPersona, esRelampago
    )
}
