package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.local.AppLocalStore
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductosUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.FavoritesUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getProductosUseCase: GetProductosUseCase,
    private val appLocalStore: AppLocalStore
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUIState(isLoading = true))
    val state: StateFlow<FavoritesUIState> = _state.asStateFlow()

    private var allProducts: List<Producto> = emptyList()

    init {
        observeFavorites()
        loadProductos()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            appLocalStore.favoriteIdsFlow.collectLatest { ids ->
                _state.update { it.copy(favoriteIds = ids) }
                applyFilter(ids)
            }
        }
    }

    private fun loadProductos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProductosUseCase().onSuccess { products ->
                allProducts = products
                applyFilter(_state.value.favoriteIds)
                _state.update { it.copy(isLoading = false) }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "No se pudieron cargar los favoritos"
                    )
                }
            }
        }
    }

    private fun applyFilter(favoriteIds: Set<Int>) {
        val favoriteProducts = allProducts.filter { favoriteIds.contains(it.id) }
        _state.update { it.copy(productos = favoriteProducts) }
    }

    fun onRetry() = loadProductos()

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
