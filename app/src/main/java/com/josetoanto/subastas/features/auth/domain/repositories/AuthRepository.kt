package com.josetoanto.subastas.features.auth.domain.repositories

import com.josetoanto.subastas.features.auth.domain.entities.AuthToken
import com.josetoanto.subastas.features.auth.domain.entities.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthToken>
    suspend fun register(nombre: String, email: String, password: String): Result<User>
    suspend fun logout()
}
