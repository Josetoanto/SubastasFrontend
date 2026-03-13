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
    val bidSuccess: Boolean = false
)
