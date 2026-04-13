package com.josetoanto.subastas.features.pujas.presentation.viewmodels

import android.content.Context
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.hardware.domain.SoundManager
import com.josetoanto.subastas.core.hardware.domain.VibrationManager
import com.josetoanto.subastas.core.service.AuctionForegroundService
import com.josetoanto.subastas.core.utils.parseIsoToEpochMillisOrNull
import com.josetoanto.subastas.core.websocket.WebSocketManager
import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductoDetailUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.CreatePujaUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.GetGanadorUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.GetPujasByProductoUseCase
import com.josetoanto.subastas.features.pujas.presentation.screens.PujasUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BASE_URL = "http://3.211.145.251:8000"

@HiltViewModel
class PujasViewModel @Inject constructor(
    private val getPujasByProductoUseCase: GetPujasByProductoUseCase,
    private val createPujaUseCase: CreatePujaUseCase,
    private val getGanadorUseCase: GetGanadorUseCase,
    private val getProductoDetailUseCase: GetProductoDetailUseCase,
    private val webSocketManager: WebSocketManager,
    private val soundManager: SoundManager,
    private val vibrationManager: VibrationManager,
    private val tokenDataStore: TokenDataStore,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _state = MutableStateFlow(PujasUIState())
    val state: StateFlow<PujasUIState> = _state.asStateFlow()

    init {
        loadPujas()
        loadGanador()
        loadProductoInfo()
        connectWebSocket()
    }

    private fun connectWebSocket() {
        webSocketManager.connect(productId, BASE_URL)
        viewModelScope.launch {
            webSocketManager.messages.collect {
                loadPujas()
            }
        }
    }

    private fun loadProductoInfo() {
        viewModelScope.launch {
            getProductoDetailUseCase(productId).onSuccess { detail ->
                _state.update {
                    it.copy(
                        esRelampago = detail.esRelampago,
                        fechaFin = detail.fechaFin,
                        nombreProducto = detail.nombre
                    )
                }
                if (detail.esRelampago || detail.status == "activo") {
                    startForegroundService(detail.nombre, detail.fechaFin, detail.esRelampago)
                }
                if (detail.esRelampago) {
                    startCountdown(detail.fechaFin)
                }
            }
        }
    }

    private fun startForegroundService(nombre: String, fechaFin: String, esRelampago: Boolean) {
        val intent = AuctionForegroundService.startIntent(context, productId, nombre, fechaFin, esRelampago)
        context.startForegroundService(intent)
    }

    private fun startCountdown(fechaFin: String) {
        viewModelScope.launch {
            val endMillis = parseIsoToEpochMillisOrNull(fechaFin)
            if (endMillis == null || endMillis <= 0L) {
                _state.update { it.copy(isExpired = false, errorMessage = null) }
                return@launch
            }
            while (true) {
                val remaining = (endMillis - System.currentTimeMillis()) / 1000L
                if (remaining <= 0L) {
                    _state.update { it.copy(countdownSeconds = 0L, isExpired = true) }
                    break
                }
                _state.update { it.copy(countdownSeconds = remaining) }
                delay(1000L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
        soundManager.release()
        context.stopService(AuctionForegroundService.stopIntent(context))
    }

    fun onCantidadChange(value: String) = _state.update { it.copy(cantidadPuja = value, errorMessage = null) }

    fun loadPujas() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPujasByProductoUseCase(productId)
                .onSuccess { pujas -> _state.update { it.copy(isLoading = false, pujas = pujas) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    private fun loadGanador() {
        viewModelScope.launch {
            getGanadorUseCase(productId).onSuccess { ganador ->
                _state.update { it.copy(ganador = ganador) }
                val currentUserId = tokenDataStore.getUserId().first()
                if (currentUserId != null && ganador.usuarioId == currentUserId) {
                    soundManager.playWinSound()
                    vibrationManager.vibrateOnWin()
                    _state.update { it.copy(triggerWinFeedback = true) }
                }
            }
        }
    }

    fun placeBid() {
        val cantidad = _state.value.cantidadPuja.toDoubleOrNull()
        if (cantidad == null || cantidad <= 0) {
            _state.update { it.copy(errorMessage = "Ingresa un monto válido") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isBidding = true, errorMessage = null) }
            createPujaUseCase(productId, cantidad)
                .onSuccess {
                    _state.update { it.copy(isBidding = false, cantidadPuja = "", bidSuccess = true) }
                }
                .onFailure { e ->
                    vibrationManager.vibrateOnOutbid()
                    _state.update {
                        it.copy(
                            isBidding = false,
                            errorMessage = e.message ?: "Error al realizar puja",
                            triggerOutbidFeedback = true
                        )
                    }
                }
        }
    }

    fun resetBidSuccess() = _state.update { it.copy(bidSuccess = false) }
    fun resetWinFeedback() = _state.update { it.copy(triggerWinFeedback = false) }
    fun resetOutbidFeedback() = _state.update { it.copy(triggerOutbidFeedback = false) }
}
