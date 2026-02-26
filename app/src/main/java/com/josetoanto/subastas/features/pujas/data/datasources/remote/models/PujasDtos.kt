package com.josetoanto.subastas.features.pujas.data.datasources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePujaRequestDto(
    @SerialName("producto_id") val productoId: Int,
    @SerialName("cantidad") val cantidad: Double
)

@Serializable
data class PujaDto(
    @SerialName("id") val id: Int,
    @SerialName("producto_id") val productoId: Int,
    @SerialName("usuario_id") val usuarioId: Int,
    @SerialName("nombre_postor") val nombrePostor: String,
    @SerialName("cantidad") val cantidad: Double,
    @SerialName("fecha") val fecha: String
)

@Serializable
data class GanadorDto(
    @SerialName("producto_id") val productoId: Int,
    @SerialName("usuario_id") val usuarioId: Int,
    @SerialName("nombre_ganador") val nombreGanador: String,
    @SerialName("cantidad_ganadora") val cantidadGanadora: Double,
    @SerialName("fecha") val fecha: String
)
