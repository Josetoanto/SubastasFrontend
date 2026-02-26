package com.josetoanto.subastas.features.auth.domain.usecases

import com.josetoanto.subastas.features.auth.domain.entities.AuthToken
import com.josetoanto.subastas.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthToken> =
        repository.login(email, password)
}
