package com.josetoanto.subastas.features.productos.presentation.screens

import com.josetoanto.subastas.features.productos.domain.entities.Producto

data class HomeUIState(
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
