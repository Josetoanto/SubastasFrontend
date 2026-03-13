package com.josetoanto.subastas.features.pujas.presentation.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.utils.toReadableMessage
import com.josetoanto.subastas.core.websocket.WebSocketManager
import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductoDetailUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.CreatePujaUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.GetGanadorUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.GetPujasByProductoUseCase
import com.josetoanto.subastas.features.pujas.presentation.screens.PujasUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

private const val BASE_URL = "http://3.211.145.251:8000"

@Serializable
private data class NuevaPujaWsMessage(
    val evento: String,
    val usuario_id: Int? = null,
    val cantidad: Double? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PujasViewModel @Inject constructor(
    private val getPujasByProductoUseCase: GetPujasByProductoUseCase,
    private val createPujaUseCase: CreatePujaUseCase,
    private val getGanadorUseCase: GetGanadorUseCase,
    private val getProductoDetailUseCase: GetProductoDetailUseCase,
    private val webSocketManager: WebSocketManager,
    private val tokenDataStore: TokenDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])
    private val json = Json { ignoreUnknownKeys = true }
    private var currentUserId: Int? = null

    private val _state = MutableStateFlow(PujasUIState())
    val state: StateFlow<PujasUIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            currentUserId = tokenDataStore.getUserId().first()
            fetchPujas()
            loadGanador()
            connectWebSocket()
            loadProductoAndScheduleEnd()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadProductoAndScheduleEnd() {
        viewModelScope.launch {
            getProductoDetailUseCase(productId)
                .onSuccess { producto ->
                    try {
                        val fechaFin = LocalDateTime.parse(producto.fechaFin)
                        val fechaFinMs = fechaFin
                            .atZone(ZoneId.of("America/Mexico_City"))
                            .toInstant()
                            .toEpochMilli()
                        scheduleEndCheck(fechaFinMs)
                    } catch (e: Exception) {
                    }
                }
        }
    }

    private fun scheduleEndCheck(fechaFinMs: Long) {
        viewModelScope.launch {
            val ahora = System.currentTimeMillis()
            val espera = fechaFinMs - ahora
            if (espera > 0) delay(espera)
            delay(1_000L)
            checkGanadorAndNotify()
        }
    }

    private fun connectWebSocket() {
        webSocketManager.connect(productId, BASE_URL)
        viewModelScope.launch {
            webSocketManager.messages.collect { message ->
                try {
                    val wsMessage = json.decodeFromString<NuevaPujaWsMessage>(message)
                    when (wsMessage.evento) {
                        "nueva_puja" -> {
                            val yoTeniaPuja = _state.value.pujas.any { it.usuarioId == currentUserId }
                            val fuePujaDeOtro = wsMessage.usuario_id != currentUserId
                            fetchPujas()
                            if (yoTeniaPuja && fuePujaDeOtro) {
                                _state.update { it.copy(fuiSuperado = true) }
                            }
                        }
                        "subasta_finalizada" -> checkGanadorAndNotify()
                    }
                } catch (e: Exception) { }
            }
        }
    }

    private suspend fun checkGanadorAndNotify() {
        getGanadorUseCase(productId)
            .onSuccess { ganador ->
                val yoGane = ganador.usuarioId == currentUserId
                _state.update {
                    it.copy(
                        ganador = ganador,
                        yoGane = yoGane,
                        acabaDeGanar = yoGane
                    )
                }
            }
    }

    private fun loadGanador() {
        viewModelScope.launch {
            getGanadorUseCase(productId)
                .onSuccess { ganador ->
                    val yoGane = ganador.usuarioId == currentUserId
                    _state.update { it.copy(ganador = ganador, yoGane = yoGane) }
                }
        }
    }

    private suspend fun fetchPujas() {
        _state.update { it.copy(isLoading = true) }
        getPujasByProductoUseCase(productId)
            .onSuccess { pujas -> _state.update { it.copy(isLoading = false, pujas = pujas) } }
            .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
    }

    fun loadPujas() {
        viewModelScope.launch { fetchPujas() }
    }

    fun onCantidadChange(value: String) =
        _state.update { it.copy(cantidadPuja = value, errorMessage = null) }

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
                    _state.update { it.copy(isBidding = false, cantidadPuja = "") }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isBidding = false,
                            errorMessage = e.toReadableMessage()
                        )
                    }
                }
        }
    }


    fun resetBidSuccess() = _state.update { it.copy(bidSuccess = false) }
    fun resetFuiSuperado() = _state.update { it.copy(fuiSuperado = false) }
    fun resetYoGane() = _state.update { it.copy(yoGane = false) }
    fun resetAcabaDeGanar() = _state.update { it.copy(acabaDeGanar = false) }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}