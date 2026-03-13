package com.josetoanto.subastas.features.pujas.domain.entities

data class Puja(
    val id: Int,
    val productoId: Int,
    val usuarioId: Int,
    val nombrePostor: String,
    val cantidad: Double,
    val fecha: String,
    val isPending: Boolean = false
)

data class Ganador(
    val productoId: Int,
    val usuarioId: Int,
    val nombreGanador: String,
    val cantidadGanadora: Double,
    val fecha: String
)
