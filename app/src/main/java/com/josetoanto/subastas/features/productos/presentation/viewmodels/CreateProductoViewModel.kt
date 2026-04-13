package com.josetoanto.subastas.features.productos.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.hardware.domain.FeatureManager
import com.josetoanto.subastas.core.local.AppLocalStore
import com.josetoanto.subastas.core.location.LocationProvider
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
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import java.io.File
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateProductoViewModel @Inject constructor(
    private val createProductoUseCase: CreateProductoUseCase,
    private val featureManager: FeatureManager,
    private val locationProvider: LocationProvider,
    private val appLocalStore: AppLocalStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val hasCameraFeature: Boolean = featureManager.hasCameraFeature()

    private val _state = MutableStateFlow(CreateProductoUIState())
    val state: StateFlow<CreateProductoUIState> = _state.asStateFlow()

    fun onNombreChange(value: String) = _state.update { it.copy(nombre = value) }
    fun onDescripcionChange(value: String) = _state.update { it.copy(descripcion = value) }
    fun onPrecioInicialChange(value: String) = _state.update { it.copy(precioInicial = value) }
    fun onFechaInicioChange(value: String) = _state.update { it.copy(fechaInicio = value) }
    fun onFechaFinChange(value: String) = _state.update { it.copy(fechaFin = value) }
    fun onImageSelected(uri: Uri) = _state.update { it.copy(imageUri = uri) }
    fun onEntregaEnPersonaChange(value: Boolean) = _state.update { it.copy(entregaEnPersona = value) }
    fun onEsRelampagoChange(value: Boolean) {
        if (!value) {
            _state.update { it.copy(esRelampago = false) }
            return
        }

        val nowIso = nowIsoLocal()
        val plusTwoIso = plusMinutesIsoLocal(2)
        _state.update {
            it.copy(
                esRelampago = true,
                fechaInicio = nowIso,
                fechaFin = plusTwoIso
            )
        }
    }

    fun resetSuccess() = _state.update { it.copy(isSuccess = false) }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true) }
            locationProvider.getCurrentLocation()
                .onSuccess { ll ->
                    _state.update { it.copy(latitud = ll.lat, longitud = ll.lon, isLoadingLocation = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingLocation = false, errorMessage = "No se pudo obtener la ubicación") }
                }
        }
    }

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
            runCatching {
                withTimeout(20_000) {
                    createProductoUseCase(
                        nombre = s.nombre,
                        descripcion = s.descripcion,
                        precioInicial = precio,
                        imagenUrl = imagePath,
                        fechaInicio = s.fechaInicio,
                        fechaFin = s.fechaFin,
                        latitud = s.latitud,
                        longitud = s.longitud,
                        ciudad = null,
                        entregaEnPersona = s.entregaEnPersona,
                        esRelampago = s.esRelampago
                    ).getOrThrow()
                }
            }.onSuccess { created ->
                appLocalStore.recordCreatedAuction(created.id)
                _state.update { st -> st.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                val timeoutLike = e is TimeoutCancellationException || e is SocketTimeoutException || e.cause is SocketTimeoutException
                if (timeoutLike) {
                    _state.update { st -> st.copy(isLoading = false, isSuccess = true) }
                } else {
                    _state.update { st -> st.copy(isLoading = false, errorMessage = e.toReadableMessage()) }
                }
            }
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

    private fun nowIsoLocal(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now()
                .withSecond(0)
                .withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US))
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        @Suppress("SimpleDateFormat")
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(cal.time)
    }

    private fun plusMinutesIsoLocal(minutes: Int): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now()
                .withSecond(0)
                .withNano(0)
                .plusMinutes(minutes.toLong())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US))
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, minutes)
        }
        @Suppress("SimpleDateFormat")
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(cal.time)
    }
}
