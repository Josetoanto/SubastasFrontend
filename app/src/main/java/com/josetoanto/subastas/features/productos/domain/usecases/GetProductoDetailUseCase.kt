package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class GetProductoDetailUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(id: Int): Result<ProductoDetail> = repository.getProductoById(id)
}
