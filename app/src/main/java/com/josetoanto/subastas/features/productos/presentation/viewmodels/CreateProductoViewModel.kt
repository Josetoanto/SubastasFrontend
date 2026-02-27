package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.productos.domain.usecases.CreateProductoUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.CreateProductoUIState
import com.josetoanto.subastas.core.utils.toReadableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProductoViewModel @Inject constructor(
    private val createProductoUseCase: CreateProductoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProductoUIState())
    val state: StateFlow<CreateProductoUIState> = _state.asStateFlow()

    fun onNombreChange(value: String) = _state.update { it.copy(nombre = value) }
    fun onDescripcionChange(value: String) = _state.update { it.copy(descripcion = value) }
    fun onPrecioInicialChange(value: String) = _state.update { it.copy(precioInicial = value) }
    fun onImagenUrlChange(value: String) = _state.update { it.copy(imagenUrl = value) }
    fun onFechaInicioChange(value: String) = _state.update { it.copy(fechaInicio = value) }
    fun onFechaFinChange(value: String) = _state.update { it.copy(fechaFin = value) }
    fun resetSuccess() = _state.update { it.copy(isSuccess = false) }

    fun createProducto() {
        val s = _state.value
        val precio = s.precioInicial.toDoubleOrNull()
        if (s.nombre.isBlank() || s.descripcion.isBlank() || precio == null || s.fechaInicio.isBlank() || s.fechaFin.isBlank()) {
            _state.update { it.copy(errorMessage = "Por favor completa todos los campos correctamente") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            createProductoUseCase(s.nombre, s.descripcion, precio, s.imagenUrl, s.fechaInicio, s.fechaFin)
                .onSuccess { _state.update { st -> st.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _state.update { st -> st.copy(isLoading = false, errorMessage = e.toReadableMessage()) } }
        }
    }
}
