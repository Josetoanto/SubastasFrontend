package com.josetoanto.subastas.features.profile.domain.usecases

import com.josetoanto.subastas.features.profile.domain.entities.Profile
import com.josetoanto.subastas.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(nombre: String?, password: String?): Result<Profile> =
        repository.updateMe(nombre, password)
}
