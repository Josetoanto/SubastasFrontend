package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.ProductoAnalytics
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class GetProductoAnalyticsUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(productoId: Int): Result<ProductoAnalytics> =
        repository.getProductoAnalytics(productoId)
}
