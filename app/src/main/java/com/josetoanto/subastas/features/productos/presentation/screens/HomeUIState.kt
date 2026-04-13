package com.josetoanto.subastas.features.productos.presentation.screens

import com.josetoanto.subastas.features.productos.domain.entities.Producto

data class HomeUIState(
    val productos: List<Producto> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Filtros
    val filterRadioKm: Double = 50.0,
    val filterSoloRelampago: Boolean = false,
    val filterSoloEntregaPersona: Boolean = false,
    val isUsingNearMe: Boolean = false,
    val showFilterSheet: Boolean = false,
    val locationPermissionDenied: Boolean = false,
    
    // Filtros IA
    val aiCategory: String = "Todos",
    val isCategorizingByAi: Boolean = false
)
