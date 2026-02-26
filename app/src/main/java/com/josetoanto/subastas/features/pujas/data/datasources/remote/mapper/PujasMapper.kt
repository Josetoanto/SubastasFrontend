package com.josetoanto.subastas.features.pujas.data.datasources.remote.mapper

import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.GanadorDto
import com.josetoanto.subastas.features.pujas.data.datasources.remote.models.PujaDto
import com.josetoanto.subastas.features.pujas.domain.entities.Ganador
import com.josetoanto.subastas.features.pujas.domain.entities.Puja

fun PujaDto.toDomain(): Puja = Puja(
    id = id,
    productoId = productoId,
    usuarioId = usuarioId,
    nombrePostor = nombrePostor,
    cantidad = cantidad,
    fecha = fecha
)

fun GanadorDto.toDomain(): Ganador = Ganador(
    productoId = productoId,
    usuarioId = usuarioId,
    nombreGanador = nombreGanador,
    cantidadGanadora = cantidadGanadora,
    fecha = fecha
)
