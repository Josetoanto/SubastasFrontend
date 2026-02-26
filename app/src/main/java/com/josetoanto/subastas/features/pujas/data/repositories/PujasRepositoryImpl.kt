package com.josetoanto.subastas.features.pujas.data.repositories

import com.josetoanto.subastas.features.pujas.data.datasources.remote.api.PujasApi
import com.josetoanto.subastas.features.pujas.data.datasources.remote.mapper.toDomain
import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.CreatePujaRequestDto
import com.josetoanto.subastas.features.pujas.domain.entities.Ganador
import com.josetoanto.subastas.features.pujas.domain.entities.Puja
import com.josetoanto.subastas.features.pujas.domain.repositories.PujasRepository
import javax.inject.Inject

class PujasRepositoryImpl @Inject constructor(
    private val api: PujasApi
) : PujasRepository {

    override suspend fun createPuja(productoId: Int, cantidad: Double): Result<Puja> = runCatching {
        api.createPuja(CreatePujaRequestDto(productoId = productoId, cantidad = cantidad)).toDomain()
    }

    override suspend fun getPujasByProducto(productoId: Int): Result<List<Puja>> = runCatching {
        api.getPujasByProducto(productoId).map { it.toDomain() }
    }

    override suspend fun getGanador(productoId: Int): Result<Ganador> = runCatching {
        api.getGanador(productoId).toDomain()
    }
}
