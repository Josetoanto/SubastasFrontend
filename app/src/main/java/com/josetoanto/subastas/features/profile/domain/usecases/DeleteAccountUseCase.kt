package com.josetoanto.subastas.features.profile.domain.usecases

import com.josetoanto.subastas.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteMe()
}
