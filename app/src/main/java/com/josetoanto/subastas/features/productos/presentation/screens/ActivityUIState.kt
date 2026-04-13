package com.josetoanto.subastas.features.productos.presentation.screens

import com.josetoanto.subastas.core.local.ActivitySummary

data class ActivityUIState(
    val summary: ActivitySummary = ActivitySummary(),
    val isLoading: Boolean = true
)
