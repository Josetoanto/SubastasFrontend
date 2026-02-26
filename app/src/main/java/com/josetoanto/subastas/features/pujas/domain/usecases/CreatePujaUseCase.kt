package com.josetoanto.subastas.features.pujas.domain.usecases

import com.josetoanto.subastas.features.pujas.domain.entities.Puja
import com.josetoanto.subastas.features.pujas.domain.repositories.PujasRepository
import javax.inject.Inject

class CreatePujaUseCase @Inject constructor(
    private val repository: PujasRepository
) {
    suspend operator fun invoke(productoId: Int, cantidad: Double): Result<Puja> =
        repository.createPuja(productoId, cantidad)
}
