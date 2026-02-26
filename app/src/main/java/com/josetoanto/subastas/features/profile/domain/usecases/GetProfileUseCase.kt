package com.josetoanto.subastas.features.profile.domain.usecases

import com.josetoanto.subastas.features.profile.domain.entities.Profile
import com.josetoanto.subastas.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<Profile> = repository.getMe()
}
