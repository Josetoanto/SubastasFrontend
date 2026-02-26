package com.josetoanto.subastas.features.pujas.domain.usecases

import com.josetoanto.subastas.features.pujas.domain.entities.Ganador
import com.josetoanto.subastas.features.pujas.domain.repositories.PujasRepository
import javax.inject.Inject

class GetGanadorUseCase @Inject constructor(
    private val repository: PujasRepository
) {
    suspend operator fun invoke(productoId: Int): Result<Ganador> =
        repository.getGanador(productoId)
}
