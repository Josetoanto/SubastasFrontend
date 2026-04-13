package com.josetoanto.subastas.features.productos.domain.entities

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioInicial: Double,
    val imagenUrl: String,
    val status: String,
    val fechaInicio: String,
    val fechaFin: String,
    val precioActual: Double,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val usuarioId: Int? = null,
    val ciudad: String = "",
    val entregaEnPersona: Boolean = false,
    val esRelampago: Boolean = false
)

data class ProductoDetail(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioInicial: Double,
    val imagenUrl: String,
    val status: String,
    val fechaInicio: String,
    val fechaFin: String,
    val precioActual: Double,
    val nombreVendedor: String,
    val usuarioId: Int,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val ciudad: String = "",
    val entregaEnPersona: Boolean = false,
    val esRelampago: Boolean = false
)
