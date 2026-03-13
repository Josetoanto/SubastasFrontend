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
    val isSuccess: Boolean = false
)