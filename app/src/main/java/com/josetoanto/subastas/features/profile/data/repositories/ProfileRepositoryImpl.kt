package com.josetoanto.subastas.features.profile.data.repositories

import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.profile.data.datasources.remote.api.ProfileApi
import com.josetoanto.subastas.features.profile.data.datasources.remote.mapper.toDomain
import com.josetoanto.subastas.features.profile.data.datasources.remote.models.UpdateProfileRequestDto
import com.josetoanto.subastas.features.profile.domain.entities.Profile
import com.josetoanto.subastas.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
    private val tokenDataStore: TokenDataStore
) : ProfileRepository {

    override suspend fun getMe(): Result<Profile> = runCatching {
        api.getMe().toDomain()
    }

    override suspend fun updateMe(nombre: String?, password: String?): Result<Profile> = runCatching {
        api.updateMe(UpdateProfileRequestDto(nombre = nombre, contrasena = password)).toDomain()
    }

    override suspend fun deleteMe(): Result<Unit> = runCatching {
        api.deleteMe()
        tokenDataStore.clearToken()
    }
}
