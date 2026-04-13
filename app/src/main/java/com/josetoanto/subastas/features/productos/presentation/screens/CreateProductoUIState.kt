package com.josetoanto.subastas.features.productos.presentation.screens

import android.net.Uri

data class CreateProductoUIState(
    val nombre: String = "",
    val descripcion: String = "",
    val precioInicial: String = "",
    val imageUri: Uri? = null,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    // Geo
    val latitud: Double? = null,
    val longitud: Double? = null,
    val entregaEnPersona: Boolean = false,
    val esRelampago: Boolean = false,
    val isLoadingLocation: Boolean = false
)
