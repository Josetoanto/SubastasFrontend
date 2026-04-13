package com.josetoanto.subastas.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.auth.domain.usecases.LoginUseCase
import com.josetoanto.subastas.features.auth.domain.usecases.LogoutUseCase
import com.josetoanto.subastas.features.auth.domain.usecases.RegisterUseCase
import com.josetoanto.subastas.features.auth.presentation.screens.LoginUIState
import com.josetoanto.subastas.features.auth.presentation.screens.RegisterUIState
import com.josetoanto.subastas.core.utils.toReadableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUIState())
    val loginState: StateFlow<LoginUIState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUIState())
    val registerState: StateFlow<RegisterUIState> = _registerState.asStateFlow()

    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onRegisterNombreChange(nombre: String) {
        _registerState.update { it.copy(nombre = nombre, nombreError = null) }
    }

    fun onRegisterEmailChange(email: String) {
        _registerState.update { it.copy(email = email, emailError = null) }
    }

    fun onRegisterPasswordChange(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null, confirmPasswordError = null) }
    }

    fun onRegisterConfirmPasswordChange(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun login() {
        val state = _loginState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _loginState.update { it.copy(errorMessage = "Por favor ingresa email y contraseña") }
            return
        }
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            loginUseCase(state.email, state.password)
                .onSuccess { _loginState.update { s -> s.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _loginState.update { s -> s.copy(isLoading = false, errorMessage = e.toReadableMessage()) } }
        }
    }

    fun register() {
        val state = _registerState.value

        val nombreError = when {
            state.nombre.isBlank() -> "El nombre es obligatorio"
            state.nombre.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }

        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        val emailError = when {
            state.email.isBlank() -> "El correo es obligatorio"
            !emailPattern.matcher(state.email.trim()).matches() -> "Formato de correo no valido"
            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> "La contrasena es obligatoria"
            state.password.length < 6 -> "Minimo 6 caracteres"
            !state.password.any { it.isUpperCase() } -> "Debe contener al menos una mayuscula"
            !state.password.any { it.isDigit() } -> "Debe contener al menos un numero"
            else -> null
        }

        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "Confirma tu contrasena"
            state.password != state.confirmPassword -> "Las contrasenas no coinciden"
            else -> null
        }

        val hasError = nombreError != null || emailError != null || passwordError != null || confirmPasswordError != null

        _registerState.update {
            it.copy(
                nombreError = nombreError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                errorMessage = null
            )
        }

        if (hasError) return

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, errorMessage = null) }
            registerUseCase(state.nombre.trim(), state.email.trim(), state.password)
                .onSuccess { _registerState.update { s -> s.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _registerState.update { s -> s.copy(isLoading = false, errorMessage = e.toReadableMessage()) } }
        }
    }

    fun logout() {
        viewModelScope.launch { logoutUseCase() }
    }

    fun resetLoginSuccess() {
        _loginState.update { it.copy(isSuccess = false) }
    }

    fun resetRegisterSuccess() {
        _registerState.update { it.copy(isSuccess = false) }
    }
}
