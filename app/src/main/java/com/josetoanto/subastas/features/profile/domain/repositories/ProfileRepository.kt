package com.josetoanto.subastas.features.profile.domain.repositories

import com.josetoanto.subastas.features.profile.domain.entities.Profile

interface ProfileRepository {
    suspend fun getMe(): Result<Profile>
    suspend fun updateMe(nombre: String?, password: String?): Result<Profile>
    suspend fun deleteMe(): Result<Unit>
}
