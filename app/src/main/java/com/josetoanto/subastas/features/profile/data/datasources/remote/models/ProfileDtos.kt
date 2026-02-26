package com.josetoanto.subastas.features.profile.data.datasources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
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
