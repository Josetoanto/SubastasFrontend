package com.josetoanto.subastas.features.productos.presentation.screens

import com.josetoanto.subastas.features.productos.domain.entities.Producto

data class FlashAuctionsUIState(
    val productos: List<Producto> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
