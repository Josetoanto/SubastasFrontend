package com.josetoanto.subastas.features.profile.data.datasources.remote.api

import com.josetoanto.subastas.features.profile.data.datasources.remote.models.ProfileDto
import com.josetoanto.subastas.features.profile.data.datasources.remote.models.UpdateProfileRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApi {

    @GET("api/v1/usuarios/me")
    suspend fun getMe(): ProfileDto

    @PUT("api/v1/usuarios/me")
    suspend fun updateMe(@Body body: UpdateProfileRequestDto): ProfileDto

    @DELETE("api/v1/usuarios/me")
    suspend fun deleteMe()
}
