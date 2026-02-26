package com.josetoanto.subastas.features.auth.data.repositories

import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.auth.data.datasources.remote.api.AuthApi
import com.josetoanto.subastas.features.auth.data.datasources.remote.mapper.toDomain
import com.josetoanto.subastas.features.auth.data.datasources.remote.models.LoginRequestDto
import com.josetoanto.subastas.features.auth.data.datasources.remote.models.RegisterRequestDto
import com.josetoanto.subastas.features.auth.domain.entities.AuthToken
import com.josetoanto.subastas.features.auth.domain.entities.User
import com.josetoanto.subastas.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthToken> = runCatching {
        val response = authApi.login(LoginRequestDto(email = email, contrasena = password))
        val token = response.toDomain()
        tokenDataStore.saveToken(token.accessToken)
        token
    }

    override suspend fun register(nombre: String, email: String, password: String): Result<User> =
        runCatching {
            authApi.register(
                RegisterRequestDto(nombre = nombre, email = email, contrasena = password)
            ).toDomain()
        }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }
}
