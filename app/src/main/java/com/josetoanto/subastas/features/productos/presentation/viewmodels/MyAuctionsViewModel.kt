package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.local.AppLocalStore
import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductoDetailUseCase
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductosUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.MyAuctionsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val tokenDataStore: TokenDataStore,
    private val appLocalStore: AppLocalStore,
    private val getProductoDetailUseCase: GetProductoDetailUseCase
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

            val createdIds = appLocalStore.createdAuctionIdsFlow.first()
            val normalResult = getProductosUseCase()
            val flashResult = getProductosUseCase(soloRelampago = true)

            if (normalResult.isFailure && flashResult.isFailure) {
                val message = normalResult.exceptionOrNull()?.message
                    ?: flashResult.exceptionOrNull()?.message
                    ?: "Error al cargar tus subastas"
                _state.update { it.copy(isLoading = false, errorMessage = message) }
                return@launch
            }

            val combined = (normalResult.getOrDefault(emptyList()) + flashResult.getOrDefault(emptyList()))
                .distinctBy { it.id }

            val ownedDirect = combined.filter { product ->
                product.usuarioId == userId || createdIds.contains(product.id)
            }

            val unresolved = combined.filterNot { p ->
                ownedDirect.any { it.id == p.id }
            }

            val ownedByDetail = unresolved.map { product ->
                async {
                    getProductoDetailUseCase(product.id)
                        .getOrNull()
                        ?.takeIf { detail -> detail.usuarioId == userId }
                        ?.let { product }
                }
            }.awaitAll().filterNotNull()

            val owned = (ownedDirect + ownedByDetail).distinctBy { it.id }

            _state.update {
                it.copy(
                    isLoading = false,
                    productos = owned,
                    errorMessage = null
                )
            }
        }
    }
}
