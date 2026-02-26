package com.josetoanto.subastas.features.profile.data.datasources.remote.mapper

import com.josetoanto.subastas.features.profile.data.datasources.remote.models.ProfileDto
import com.josetoanto.subastas.features.profile.domain.entities.Profile

fun ProfileDto.toDomain(): Profile = Profile(
    id = id,
    nombre = nombre,
    email = email,
    fechaRegistro = fechaRegistro
)
