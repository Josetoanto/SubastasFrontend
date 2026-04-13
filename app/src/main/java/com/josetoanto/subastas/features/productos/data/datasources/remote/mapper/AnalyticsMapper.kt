package com.josetoanto.subastas.features.productos.data.datasources.remote.mapper

import com.josetoanto.subastas.features.productos.data.datasources.remote.models.AnalyticsDtos
import com.josetoanto.subastas.features.productos.domain.entities.PriceHistoryEntry
import com.josetoanto.subastas.features.productos.domain.entities.ProductoAnalytics

fun AnalyticsDtos.ProductoAnalyticsDto.toDomain(): ProductoAnalytics = ProductoAnalytics(
    productoId = productoId,
    nombre = nombre,
    historialPrecios = historialPrecios.map {
        PriceHistoryEntry(fecha = it.fecha, precio = it.precio, postor = it.postor)
    },
    totalPostores = totalPostores,
    pujaInicial = pujaInicial.toDoubleOrNull() ?: 0.0,
    pujaMaxima = pujaMaxima,
    pujaMinima = pujaMinima
)
