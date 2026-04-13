package com.josetoanto.subastas.features.productos.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josetoanto.subastas.core.local.AppLocalStore
import com.josetoanto.subastas.core.location.LocationProvider
import com.josetoanto.subastas.features.auth.data.datasources.local.TokenDataStore
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import com.josetoanto.subastas.features.productos.domain.usecases.CategorizeByOpenSourceUseCase
import com.josetoanto.subastas.features.productos.domain.usecases.GetProductosUseCase
import com.josetoanto.subastas.features.productos.presentation.screens.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductosUseCase: GetProductosUseCase,
    private val categorizeByOpenSourceUseCase: CategorizeByOpenSourceUseCase,
    private val locationProvider: LocationProvider,
    private val appLocalStore: AppLocalStore,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state: StateFlow<HomeUIState> = _state.asStateFlow()

    // Cache interno para no volver a descargar cuando se cambia de categoría IA
    private var allVisibleProductosCache = emptyList<Producto>()

    init {
        observeFavorites()
        loadProductos()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            appLocalStore.favoriteIdsFlow.collectLatest { favorites ->
                _state.update { it.copy(favoriteIds = favorites) }
            }
        }
    }

    fun loadProductos() {
        viewModelScope.launch {
            val s = _state.value
            val currentUserId = tokenDataStore.getUserId().first()
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getProductosUseCase(
                soloRelampago = s.filterSoloRelampago,
                soloEntregaPersona = s.filterSoloEntregaPersona
            ).onSuccess { productos ->
                val visibles = applyClientFilters(productos, currentUserId)
                allVisibleProductosCache = visibles
                
                // Si la categoría IA no es "Todos", la aplicamos de inmediato, sino solo actualizamos vista
                if (s.aiCategory != "Todos") {
                    applyAiFilterCache(visibles, s.aiCategory)
                } else {
                    _state.update { it.copy(isLoading = false, productos = visibles, isUsingNearMe = false) }
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar productos") }
            }
        }
    }

    fun onAiCategorySelected(category: String) {
        if (_state.value.aiCategory == category) return
        
        _state.update { it.copy(aiCategory = category, isCategorizingByAi = true) }
        applyAiFilterCache(allVisibleProductosCache, category)
    }

    private fun applyAiFilterCache(productosCache: List<Producto>, selectedCategory: String) {
        viewModelScope.launch {
            _state.update { it.copy(isCategorizingByAi = true) }
            
            if (selectedCategory == "Todos") {
                _state.update { 
                    it.copy(
                        productos = productosCache, 
                        isCategorizingByAi = false,
                        isLoading = false
                    ) 
                }
                return@launch
            }

            categorizeByOpenSourceUseCase(productosCache, selectedCategory)
                .onSuccess { filteredByAi ->
                    _state.update { 
                        it.copy(
                            productos = filteredByAi, 
                            isCategorizingByAi = false,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    // Si falla HF (timeouts o no token), simplemente restauramos a cache
                    _state.update { 
                        it.copy(
                            productos = productosCache,
                            isCategorizingByAi = false,
                            isLoading = false
                        ) 
                    }
                }
        }
    }

    fun loadNearMe() {
        viewModelScope.launch {
            val currentUserId = tokenDataStore.getUserId().first()
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            locationProvider.getCurrentLocation()
                .onSuccess { latLon ->
                    getProductosUseCase(
                        lat = latLon.lat,
                        lon = latLon.lon,
                        radioKm = _state.value.filterRadioKm
                    ).onSuccess { productos ->
                        val visibles = applyClientFilters(productos, currentUserId)
                        allVisibleProductosCache = visibles
                        _state.update { it.copy(isLoading = false, productos = visibles, isUsingNearMe = true, aiCategory = "Todos") }
                    }.onFailure { e ->
                        _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, locationPermissionDenied = true) }
                }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) loadNearMe()
        else _state.update { it.copy(locationPermissionDenied = true) }
    }

    fun onFilterRadioKmChange(value: Double) = _state.update { it.copy(filterRadioKm = value) }
    fun onFilterSoloRelampagoChange(value: Boolean) = _state.update { it.copy(filterSoloRelampago = value) }
    fun onFilterSoloEntregaPersonaChange(value: Boolean) = _state.update { it.copy(filterSoloEntregaPersona = value) }
    fun onToggleFilterSheet() = _state.update { it.copy(showFilterSheet = !it.showFilterSheet) }
    fun onDismissFilterSheet() = _state.update { it.copy(showFilterSheet = false) }
    fun onDismissLocationDenied() = _state.update { it.copy(locationPermissionDenied = false) }

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

    private fun applyClientFilters(
        productos: List<Producto>,
        currentUserId: Int?
    ): List<Producto> {
        return productos.filter { p ->
            !p.esRelampago &&
                (p.status.lowercase() == "activo" || p.usuarioId == currentUserId)
        }
    }
}
