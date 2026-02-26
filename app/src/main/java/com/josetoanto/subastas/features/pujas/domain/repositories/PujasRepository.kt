package com.josetoanto.subastas.features.pujas.domain.repositories

import com.josetoanto.subastas.features.pujas.domain.entities.Ganador
import com.josetoanto.subastas.features.pujas.domain.entities.Puja

interface PujasRepository {
    suspend fun createPuja(productoId: Int, cantidad: Double): Result<Puja>
    suspend fun getPujasByProducto(productoId: Int): Result<List<Puja>>
    suspend fun getGanador(productoId: Int): Result<Ganador>
}
