package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductoDetailUseCase
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductoAnalyticsUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.AnalyticsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getProductoAnalyticsUseCase: GetProductoAnalyticsUseCase,
    private val getProductoDetailUseCase: GetProductoDetailUseCase,
    private val tokenDataStore: TokenDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _state = MutableStateFlow(AnalyticsUIState())
    val state: StateFlow<AnalyticsUIState> = _state.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val currentUserId = tokenDataStore.getUserId().first()
            if (currentUserId == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "No tienes permiso para ver estos analytics") }
                return@launch
            }

            getProductoDetailUseCase(productId)
                .onSuccess { detail ->
                    if (detail.usuarioId != currentUserId) {
                        _state.update { it.copy(isLoading = false, errorMessage = "No tienes permiso para ver estos analytics") }
                        return@onSuccess
                    }

                    getProductoAnalyticsUseCase(productId)
                        .onSuccess { analytics ->
                            _state.update { it.copy(isLoading = false, analytics = analytics) }
                        }
                        .onFailure { e ->
                            _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar analytics") }
                        }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar detalle") }
                }
        }
    }
}
