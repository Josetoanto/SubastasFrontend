package com.josetoanto.subastas.features.auth.presentation.screens

data class RegisterUIState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
