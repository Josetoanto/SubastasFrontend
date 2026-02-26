package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.features.productos.domain.usecases.DeleteProductoUseCase
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductoDetailUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.ProductoDetailUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductoDetailViewModel @Inject constructor(
    private val getProductoDetailUseCase: GetProductoDetailUseCase,
    private val deleteProductoUseCase: DeleteProductoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _state = MutableStateFlow(ProductoDetailUIState())
    val state: StateFlow<ProductoDetailUIState> = _state.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getProductoDetailUseCase(productId)
                .onSuccess { detail ->
                    _state.update { it.copy(isLoading = false, producto = detail) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar detalle") }
                }
        }
    }

    fun deleteProducto() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            deleteProductoUseCase(productId)
                .onSuccess { _state.update { s -> s.copy(isLoading = false, deleteSuccess = true) } }
                .onFailure { e -> _state.update { s -> s.copy(isLoading = false, errorMessage = e.message ?: "Error al eliminar") } }
        }
    }
}
