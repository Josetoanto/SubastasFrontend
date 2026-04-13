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
    precioActual = precioActual ?: precioInicial,
    latitud = latitud,
    longitud = longitud,
        usuarioId = usuarioId,
    ciudad = ciudad ?: "",
    entregaEnPersona = entregaEnPersona ?: false,
    esRelampago = esRelampago ?: false
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
    usuarioId = usuarioId,
    latitud = latitud,
    longitud = longitud,
    ciudad = ciudad ?: "",
    entregaEnPersona = entregaEnPersona ?: false,
    esRelampago = esRelampago ?: false
)
