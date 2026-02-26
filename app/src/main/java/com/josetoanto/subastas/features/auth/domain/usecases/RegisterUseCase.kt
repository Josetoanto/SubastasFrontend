package com.josetoanto.subastas.features.auth.domain.usecases

import com.josetoanto.subastas.features.auth.domain.entities.User
import com.josetoanto.subastas.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(nombre: String, email: String, password: String): Result<User> =
        repository.register(nombre, email, password)
}
