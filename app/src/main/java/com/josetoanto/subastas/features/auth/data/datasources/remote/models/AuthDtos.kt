package com.josetoanto.subastas.features.auth.data.datasources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("contrasena") val contrasena: String
)

@Serializable
data class LoginResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String
)

@Serializable
data class RegisterRequestDto(
    @SerialName("nombre") val nombre: String,
    @SerialName("email") val email: String,
    @SerialName("contrasena") val contrasena: String
)

@Serializable
data class UserDto(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("email") val email: String,
    @SerialName("fecha_registro") val fechaRegistro: String
)

@Serializable
data class UpdateProfileRequestDto(
    @SerialName("nombre") val nombre: String? = null,
    @SerialName("contrasena") val contrasena: String? = null
)
