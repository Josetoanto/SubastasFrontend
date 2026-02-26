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
    val precioActual: Double
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
    val usuarioId: Int
)
