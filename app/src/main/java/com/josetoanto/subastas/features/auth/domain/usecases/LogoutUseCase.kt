package com.josetoanto.subastas.features.auth.domain.usecases

import com.josetoanto.subastas.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}
