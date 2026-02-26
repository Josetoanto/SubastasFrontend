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
        fechaFin: String
    ): Result<Producto> = repository.createProducto(nombre, descripcion, precioInicial, imagenUrl, fechaInicio, fechaFin)
}
