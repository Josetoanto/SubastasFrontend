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
        var hasError = false

        _registerState.update {
            it.copy(
                nombreError = null, emailError = null,
                passwordError = null, confirmPasswordError = null,
                errorMessage = null
            )
        }

        if (state.nombre.isBlank()) {
            _registerState.update { it.copy(nombreError = "El nombre es obligatorio") }
            hasError = true
        } else if (state.nombre.trim().length < 3) {
            _registerState.update { it.copy(nombreError = "El nombre debe tener al menos 3 caracteres") }
            hasError = true
        }

        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (state.email.isBlank()) {
            _registerState.update { it.copy(emailError = "El correo es obligatorio") }
            hasError = true
        } else if (!emailPattern.matcher(state.email.trim()).matches()) {
            _registerState.update { it.copy(emailError = "Formato de correo no valido") }
            hasError = true
        }

        if (state.password.isBlank()) {
            _registerState.update { it.copy(passwordError = "La contrasena es obligatoria") }
            hasError = true
        } else if (state.password.length < 6) {
            _registerState.update { it.copy(passwordError = "Minimo 6 caracteres") }
            hasError = true
        } else if (!state.password.any { it.isUpperCase() }) {
            _registerState.update { it.copy(passwordError = "Debe contener al menos una mayuscula") }
            hasError = true
        } else if (!state.password.any { it.isDigit() }) {
            _registerState.update { it.copy(passwordError = "Debe contener al menos un numero") }
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            _registerState.update { it.copy(confirmPasswordError = "Confirma tu contrasena") }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            _registerState.update { it.copy(confirmPasswordError = "Las contrasenas no coinciden") }
            hasError = true
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
