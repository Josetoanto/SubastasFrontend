package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductosUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.MyAuctionsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAuctionsViewModel @Inject constructor(
    private val getProductosUseCase: GetProductosUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(MyAuctionsUIState())
    val state: StateFlow<MyAuctionsUIState> = _state.asStateFlow()

    init {
        loadMyAuctions()
    }

    fun loadMyAuctions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = tokenDataStore.getUserId().first()
            if (userId == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "No se pudo identificar tu usuario") }
                return@launch
            }

            getProductosUseCase()
                .onSuccess { productos ->
                    val owned = productos.filter { it.usuarioId == userId }
                    _state.update { it.copy(isLoading = false, productos = owned) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar tus subastas") }
                }
        }
    }
}
