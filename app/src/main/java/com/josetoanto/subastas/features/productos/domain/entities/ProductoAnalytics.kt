package com.josetoanto.subastas.features.productos.domain.entities

data class PriceHistoryEntry(
    val fecha: String,
    val precio: Double,
    val postor: String? = null
)

data class ProductoAnalytics(
    val productoId: Int,
    val nombre: String,
    val historialPrecios: List<PriceHistoryEntry>,
    val totalPostores: Int,
    val pujaInicial: Double,
    val pujaMaxima: Double?,
    val pujaMinima: Double?
)
