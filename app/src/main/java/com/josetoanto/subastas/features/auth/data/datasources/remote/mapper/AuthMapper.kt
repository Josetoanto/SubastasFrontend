package com.josetoanto.subastas.features.auth.data.datasources.remote.mapper

import com.josetoanto.subastas.features.auth.data.datasources.remote.models.LoginResponseDto
import com.josetoanto.subastas.features.auth.data.datasources.remote.models.UserDto
import com.josetoanto.subastas.features.auth.domain.entities.AuthToken
import com.josetoanto.subastas.features.auth.domain.entities.User

fun LoginResponseDto.toDomain(): AuthToken = AuthToken(
    accessToken = accessToken,
    tokenType = tokenType
)

fun UserDto.toDomain(): User = User(
    id = id,
    nombre = nombre,
    email = email,
    fechaRegistro = fechaRegistro
)
