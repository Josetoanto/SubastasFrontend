package com.josetoanto.subastas.features.productos.data.datasources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("precio_inicial") val precioInicial: Double,
    @SerialName("imagen_url") val imagenUrl: String,
    @SerialName("status") val status: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String,
    @SerialName("precio_actual") val precioActual: Double
)

@Serializable
data class ProductoDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("precio_inicial") val precioInicial: Double,
    @SerialName("imagen_url") val imagenUrl: String,
    @SerialName("status") val status: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String,
    @SerialName("precio_actual") val precioActual: Double,
    @SerialName("nombre_vendedor") val nombreVendedor: String,
    @SerialName("usuario_id") val usuarioId: Int
)

@Serializable
data class CreateProductoRequestDto(
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("precio_inicial") val precioInicial: Double,
    @SerialName("imagen_url") val imagenUrl: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String
)

@Serializable
data class UpdateProductoRequestDto(
    @SerialName("nombre") val nombre: String? = null,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("imagen_url") val imagenUrl: String? = null
)
