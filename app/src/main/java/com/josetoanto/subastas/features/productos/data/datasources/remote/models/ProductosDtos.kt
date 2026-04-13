package com.josetoanto.subastas.features.productos.data.datasources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("precio_inicial") val precioInicial: Double,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    @SerialName("status") val status: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String,
    @SerialName("precio_actual") val precioActual: Double? = null,
    @SerialName("latitud") val latitud: Double? = null,
    @SerialName("longitud") val longitud: Double? = null,
    @SerialName("usuario_id") val usuarioId: Int? = null,
    @SerialName("ciudad") val ciudad: String? = null,
    @SerialName("entrega_en_persona") val entregaEnPersona: Boolean? = null,
    @SerialName("es_relampago") val esRelampago: Boolean? = null
)

@Serializable
data class ProductoDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("precio_inicial") val precioInicial: Double,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    @SerialName("status") val status: String,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String,
    @SerialName("precio_actual") val precioActual: Double? = null,
    @SerialName("nombre_vendedor") val nombreVendedor: String,
    @SerialName("usuario_id") val usuarioId: Int,
    @SerialName("latitud") val latitud: Double? = null,
    @SerialName("longitud") val longitud: Double? = null,
    @SerialName("ciudad") val ciudad: String? = null,
    @SerialName("entrega_en_persona") val entregaEnPersona: Boolean? = null,
    @SerialName("es_relampago") val esRelampago: Boolean? = null
)

@Serializable
data class CreateProductoRequestDto(
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String,
    @SerialName("precio_inicial") val precioInicial: Double,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    @SerialName("fecha_inicio") val fechaInicio: String,
    @SerialName("fecha_fin") val fechaFin: String
)

@Serializable
data class UpdateProductoRequestDto(
    @SerialName("nombre") val nombre: String? = null,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("imagen_url") val imagenUrl: String? = null
)
