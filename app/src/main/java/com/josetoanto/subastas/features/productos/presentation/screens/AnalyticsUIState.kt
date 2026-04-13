package com.josetoanto.subastas.features.productos.presentation.screens

import com.josetoanto.subastas.features.productos.domain.entities.ProductoAnalytics

data class AnalyticsUIState(
    val analytics: ProductoAnalytics? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
