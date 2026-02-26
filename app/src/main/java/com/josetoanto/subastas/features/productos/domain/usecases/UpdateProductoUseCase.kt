package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class UpdateProductoUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(
        id: Int,
        nombre: String? = null,
        descripcion: String? = null,
        imagenUrl: String? = null
    ): Result<Producto> = repository.updateProducto(id, nombre, descripcion, imagenUrl)
}
