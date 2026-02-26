package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class DeleteProductoUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deleteProducto(id)
}
