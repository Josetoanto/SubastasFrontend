package com.josetoanto.subastas.features.auth.domain.entities

data class AuthToken(
    val accessToken: String,
    val tokenType: String
)
