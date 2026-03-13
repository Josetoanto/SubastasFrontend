package com.josetoanto.subastas.features.pujas.data.repositories

import com.josetoanto.subastas.core.database.dao.PujaDao
import com.josetoanto.subastas.core.database.entities.toDomain
import com.josetoanto.subastas.core.database.entities.toEntity
import com.josetoanto.subastas.features.pujas.data.datasources.remote.api.PujasApi
import com.josetoanto.subastas.features.pujas.data.datasources.remote.mapper.toDomain
import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.CreatePujaRequestDto
import com.josetoanto.subastas.features.pujas.domain.entities.Ganador
import com.josetoanto.subastas.features.pujas.domain.entities.Puja
import com.josetoanto.subastas.features.pujas.domain.repositories.PujasRepository
import javax.inject.Inject

class PujasRepositoryImpl @Inject constructor(
    private val api: PujasApi,
    private val pujaDao: PujaDao
) : PujasRepository {

    override suspend fun createPuja(productoId: Int, cantidad: Double): Result<Puja> = runCatching {
        val puja = api.createPuja(CreatePujaRequestDto(productoId = productoId, cantidad = cantidad)).toDomain()
        pujaDao.insert(puja.toEntity())
        puja
    }

    override suspend fun getPujasByProducto(productoId: Int): Result<List<Puja>> {
        val cached = pujaDao.getPujasByProductoId(productoId).map { it.toDomain() }

        return try {
            val remote = api.getPujasByProducto(productoId).map { it.toDomain() }
            pujaDao.clearByProductoId(productoId)
            pujaDao.insertAll(remote.map { it.toEntity() })
            Result.success(remote)
        } catch (e: Exception) {
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getGanador(productoId: Int): Result<Ganador> = runCatching {
        api.getGanador(productoId).toDomain()
    }
}
