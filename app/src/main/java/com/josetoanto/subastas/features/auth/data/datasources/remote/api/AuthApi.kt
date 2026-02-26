package com.josetoanto.subastas.features.auth.data.datasources.remote.api

import com.josetoanto.subastas.features.auth.data.datasources.remote.models.LoginRequestDto
import com.josetoanto.subastas.features.auth.data.datasources.remote.models.LoginResponseDto
import com.josetoanto.subastas.features.auth.data.datasources.remote.models.RegisterRequestDto
import com.josetoanto.subastas.features.auth.data.datasources.remote.models.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/usuarios/register")
    suspend fun register(@Body body: RegisterRequestDto): UserDto

    @POST("api/v1/usuarios/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto
}
