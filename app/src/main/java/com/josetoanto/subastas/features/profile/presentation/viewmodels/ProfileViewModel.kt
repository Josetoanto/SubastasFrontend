package com.josetoanto.subastas.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.auth.domain.repositories.AuthRepository
import com.josetoanto.subastas.features.profile.domain.usecases.DeleteAccountUseCase
import com.josetoanto.subastas.features.profile.domain.usecases.GetProfileUseCase
import com.josetoanto.subastas.features.profile.domain.usecases.UpdateProfileUseCase
import com.josetoanto.subastas.features.profile.presentation.screens.ProfileUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUIState())
    val state: StateFlow<ProfileUIState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun onEditNombreChange(value: String) = _state.update { it.copy(editNombre = value) }
    fun onEditPasswordChange(value: String) = _state.update { it.copy(editPassword = value) }
    fun resetUpdateSuccess() = _state.update { it.copy(updateSuccess = false) }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getProfileUseCase()
                .onSuccess { profile ->
                    _state.update { it.copy(isLoading = false, profile = profile, editNombre = profile.nombre) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar perfil") }
                }
        }
    }

    fun updateProfile() {
        val s = _state.value
        val nombre = s.editNombre.takeIf { it.isNotBlank() }
        val password = s.editPassword.takeIf { it.isNotBlank() }
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true, errorMessage = null) }
            updateProfileUseCase(nombre, password)
                .onSuccess { profile ->
                    _state.update { it.copy(isUpdating = false, profile = profile, updateSuccess = true, editPassword = "") }
                }
                .onFailure { e ->
                    _state.update { it.copy(isUpdating = false, errorMessage = e.message ?: "Error al actualizar perfil") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update { it.copy(deleteSuccess = true) }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            deleteAccountUseCase()
                .onSuccess { _state.update { it.copy(isLoading = false, deleteSuccess = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al eliminar cuenta") } }
        }
    }
}
