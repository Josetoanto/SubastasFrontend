package com.josetoanto.subastas.features.auth.domain.entities

data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val fechaRegistro: String
)
