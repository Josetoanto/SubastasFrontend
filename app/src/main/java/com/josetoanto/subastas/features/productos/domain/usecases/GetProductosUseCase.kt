package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.repositories.ProductosRepository
import javax.inject.Inject

class GetProductosUseCase @Inject constructor(
    private val repository: ProductosRepository
) {
    suspend operator fun invoke(
        ciudad: String? = null,
        lat: Double? = null,
        lon: Double? = null,
        radioKm: Double? = null,
        soloRelampago: Boolean = false,
        soloEntregaPersona: Boolean = false
    ): Result<List<Producto>> = repository.getProductos(
        ciudad = ciudad,
        lat = lat,
        lon = lon,
        radioKm = radioKm,
        soloRelampago = soloRelampago,
        soloEntregaPersona = soloEntregaPersona
    )
}
