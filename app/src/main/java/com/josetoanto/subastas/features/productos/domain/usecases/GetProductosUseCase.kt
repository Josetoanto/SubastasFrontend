package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class GetProductosUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(): Result<List<Producto>> = repository.getProductos()
}
