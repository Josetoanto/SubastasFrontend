package com.josetoanto.subastas.features.pujas.domain.usecases

import com.josetoanto.subastas.features.pujas.domain.entities.Puja
import com.josetoanto.subastas.features.pujas.domain.repositories.PujasRepository
import javax.inject.Inject

class GetPujasByProductoUseCase @Inject constructor(
    private val repository: PujasRepository
) {
    suspend operator fun invoke(productoId: Int): Result<List<Puja>> =
        repository.getPujasByProducto(productoId)
}
