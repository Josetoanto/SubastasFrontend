package com.josetoanto.subastas.features.productos.presentation.screens

import com.josetoanto.subastas.features.productos.domain.entities.ProductoDetail

data class ProductoDetailUIState(
    val producto: ProductoDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val deleteSuccess: Boolean = false,
    val isOwner: Boolean = false
)
