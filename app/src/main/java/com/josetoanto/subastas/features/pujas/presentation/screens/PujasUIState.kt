package com.josetoanto.subastas.features.pujas.presentation.screens

import com.josetoanto.subastas.features.pujas.domain.entities.Ganador
import com.josetoanto.subastas.features.pujas.domain.entities.Puja

data class PujasUIState(
    val pujas: List<Puja> = emptyList(),
    val ganador: Ganador? = null,
    val cantidadPuja: String = "",
    val isLoading: Boolean = false,
    val isBidding: Boolean = false,
    val errorMessage: String? = null,
    val bidSuccess: Boolean = false,
    // Hardware feedback
    val triggerWinFeedback: Boolean = false,
    val triggerOutbidFeedback: Boolean = false,
    // Flash auction / countdown
    val esRelampago: Boolean = false,
    val fechaFin: String = "",
    val countdownSeconds: Long = 0L,
    val isExpired: Boolean = false,
    // Product info for service
    val nombreProducto: String = ""
)
