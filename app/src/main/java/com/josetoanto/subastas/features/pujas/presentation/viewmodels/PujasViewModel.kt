package com.josetoanto.subastas.features.pujas.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        viewModelScope.launch {
            _state.update { it.copy(isBidding = true, errorMessage = null) }
            createPujaUseCase(productId, cantidad)
                .onSuccess { puja ->
                    _state.update { it.copy(isBidding = false, bidSuccess = true, cantidadPuja = "", pujas = listOf(puja) + it.pujas) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isBidding = false, errorMessage = e.message ?: "Error al realizar puja") }
                }
        }
    }

    fun resetBidSuccess() = _state.update { it.copy(bidSuccess = false) }
}
