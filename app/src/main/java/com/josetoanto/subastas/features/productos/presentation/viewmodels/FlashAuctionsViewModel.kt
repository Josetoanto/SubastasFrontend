package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.local.AppLocalStore
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductosUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.FlashAuctionsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FlashAuctionsViewModel @Inject constructor(
    private val getProductosUseCase: GetProductosUseCase,
    private val appLocalStore: AppLocalStore
) : ViewModel() {

    private val _state = MutableStateFlow(FlashAuctionsUIState(isLoading = true))
    val state: StateFlow<FlashAuctionsUIState> = _state.asStateFlow()

    init {
        observeFavorites()
        loadFlashAuctions()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            appLocalStore.favoriteIdsFlow.collectLatest { ids ->
                _state.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    fun loadFlashAuctions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProductosUseCase(soloRelampago = true).onSuccess { products ->
                _state.update {
                    it.copy(
                        productos = products,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "No se pudieron cargar las subastas relampago"
                    )
                }
            }
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            appLocalStore.toggleFavorite(productId)
            appLocalStore.recordFavoriteAction()
        }
    }

    fun onProductOpened(productId: Int) {
        viewModelScope.launch {
            appLocalStore.recordProductOpened(productId)
        }
    }
}
