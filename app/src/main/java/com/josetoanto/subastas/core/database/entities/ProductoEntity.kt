package com.josetoanto.subastas.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.josetoanto.subastas.features.productos.domain.entities.Producto

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioInicial: Double,
    val imagenUrl: String,
    val status: String,
    val fechaInicio: String,
    val fechaFin: String,
    val precioActual: Double,
    val latitud: Double?,
    val longitud: Double?,
    val usuarioId: Int?,
    val ciudad: String,
    val entregaEnPersona: Boolean,
    val esRelampago: Boolean
)

fun ProductoEntity.toDomain(): Producto = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precioInicial = precioInicial,
    imagenUrl = imagenUrl,
    status = status,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    precioActual = precioActual,
    latitud = latitud,
    longitud = longitud,
    usuarioId = usuarioId,
    ciudad = ciudad,
    entregaEnPersona = entregaEnPersona,
    esRelampago = esRelampago
)

fun Producto.toEntity(): ProductoEntity = ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precioInicial = precioInicial,
    imagenUrl = imagenUrl,
    status = status,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    precioActual = precioActual,
    latitud = latitud,
    longitud = longitud,
    usuarioId = usuarioId,
    ciudad = ciudad,
    entregaEnPersona = entregaEnPersona,
    esRelampago = esRelampago
)
