package com.josetoanto.subastas.features.pujas.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.pujas.domain.entities.Puja
import com.josetoanto.subastas.features.pujas.domain.usecases.CreatePujaUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.GetGanadorUseCase
import com.josetoanto.subastas.features.pujas.domain.usecases.GetPujasByProductoUseCase
import com.josetoanto.subastas.features.pujas.presentation.screens.PujasUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PujasViewModel @Inject constructor(
    private val getPujasByProductoUseCase: GetPujasByProductoUseCase,
    private val createPujaUseCase: CreatePujaUseCase,
    private val getGanadorUseCase: GetGanadorUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val OPTIMISTIC_BID_ID = -1
        private const val OPTIMISTIC_USER_ID = 0
    }

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _state = MutableStateFlow(PujasUIState())
    val state: StateFlow<PujasUIState> = _state.asStateFlow()

    init {
        loadPujas()
        loadGanador()
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
            getGanadorUseCase(productId)
                .onSuccess { ganador -> _state.update { it.copy(ganador = ganador) } }
                // ganador puede no existir aún (subasta en curso), ignoramos el error
        }
    }

    fun placeBid() {
        val cantidad = _state.value.cantidadPuja.toDoubleOrNull()
        if (cantidad == null || cantidad <= 0) {
            _state.update { it.copy(errorMessage = "Ingresa un monto válido") }
            return
        }
        // Save previous state for rollback
        val previousPujas = _state.value.pujas
        // Optimistic update: add placeholder bid immediately
        val optimisticPuja = Puja(
            id = OPTIMISTIC_BID_ID,
            productoId = productId,
            usuarioId = OPTIMISTIC_USER_ID,
            nombrePostor = "Tú",
            cantidad = cantidad,
            fecha = ""
        )
        _state.update { it.copy(isBidding = true, errorMessage = null, pujas = listOf(optimisticPuja) + it.pujas) }
        viewModelScope.launch {
            createPujaUseCase(productId, cantidad)
                .onSuccess { puja ->
                    // Replace optimistic placeholder with real bid from server
                    _state.update { state ->
                        state.copy(
                            isBidding = false,
                            bidSuccess = true,
                            cantidadPuja = "",
                            pujas = listOf(puja) + state.pujas.filter { it.id != OPTIMISTIC_BID_ID }
                        )
                    }
                }
                .onFailure { e ->
                    // Rollback: restore previous pujas and show error
                    _state.update { it.copy(isBidding = false, errorMessage = e.message ?: "Error al realizar puja", pujas = previousPujas) }
                }
        }
    }

    fun resetBidSuccess() = _state.update { it.copy(bidSuccess = false) }
}
