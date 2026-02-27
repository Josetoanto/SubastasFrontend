package com.josetoanto.subastas.features.productos.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.utils.toReadableMessage
import com.josetoanto.subastas.features.productos.domain.usecases.CreateProductoUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.CreateProductoUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateProductoViewModel @Inject constructor(
    private val createProductoUseCase: CreateProductoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProductoUIState())
    val state: StateFlow<CreateProductoUIState> = _state.asStateFlow()

    fun onNombreChange(value: String) = _state.update { it.copy(nombre = value) }
    fun onDescripcionChange(value: String) = _state.update { it.copy(descripcion = value) }
    fun onPrecioInicialChange(value: String) = _state.update { it.copy(precioInicial = value) }
    fun onFechaInicioChange(value: String) = _state.update { it.copy(fechaInicio = value) }
    fun onFechaFinChange(value: String) = _state.update { it.copy(fechaFin = value) }
    fun onImageSelected(uri: Uri) = _state.update { it.copy(imageUri = uri) }
    fun resetSuccess() = _state.update { it.copy(isSuccess = false) }

    fun createProducto() {
        val s = _state.value
        val precio = s.precioInicial.toDoubleOrNull()
        if (s.nombre.isBlank() || s.descripcion.isBlank() || precio == null ||
            s.fechaInicio.isBlank() || s.fechaFin.isBlank()
        ) {
            _state.update { it.copy(errorMessage = "Por favor completa todos los campos correctamente") }
            return
        }

        val imagePath = s.imageUri?.let { getRealPathFromUri(context, it) } ?: ""

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            createProductoUseCase(s.nombre, s.descripcion, precio, imagePath, s.fechaInicio, s.fechaFin)
                .onSuccess { _state.update { st -> st.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _state.update { st -> st.copy(isLoading = false, errorMessage = e.toReadableMessage()) } }
        }
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String {
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile.absolutePath
    }
}