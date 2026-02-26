package com.josetoanto.subastas.features.profile.presentation.screens

import com.josetoanto.subastas.features.profile.domain.entities.Profile

data class ProfileUIState(
    val profile: Profile? = null,
    val editNombre: String = "",
    val editPassword: String = "",
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)
