package com.josetoanto.subastas.features.productos.data.datasources.remote.mapper

import com.josetoanto.subastas.features.productos.data.datasources.remote.models.ProductoDetailDto
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.ProductoDto
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail

fun ProductoDto.toDomain(): Producto = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion ?: "",
    precioInicial = precioInicial,
    imagenUrl = imagenUrl ?: "",
    status = status,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    precioActual = precioActual ?: precioInicial
)

fun ProductoDetailDto.toDomain(): ProductoDetail = ProductoDetail(
    id = id,
    nombre = nombre,
    descripcion = descripcion ?: "",
    precioInicial = precioInicial,
    imagenUrl = imagenUrl ?: "",
    status = status,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    precioActual = precioActual ?: precioInicial,
    nombreVendedor = nombreVendedor,
    usuarioId = usuarioId
)
