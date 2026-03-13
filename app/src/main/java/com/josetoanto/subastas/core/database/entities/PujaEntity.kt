package com.josetoanto.subastas.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.josetoanto.subastas.features.pujas.domain.entities.Puja

@Entity(tableName = "pujas")
data class PujaEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "producto_id") val productoId: Int,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "nombre_postor") val nombrePostor: String,
    @ColumnInfo(name = "cantidad") val cantidad: Double,
    @ColumnInfo(name = "fecha") val fecha: String
)

fun PujaEntity.toDomain(): Puja = Puja(
    id = id,
    productoId = productoId,
    usuarioId = usuarioId,
    nombrePostor = nombrePostor,
    cantidad = cantidad,
    fecha = fecha
)

fun Puja.toEntity(): PujaEntity = PujaEntity(
    id = id,
    productoId = productoId,
    usuarioId = usuarioId,
    nombrePostor = nombrePostor,
    cantidad = cantidad,
    fecha = fecha
)
