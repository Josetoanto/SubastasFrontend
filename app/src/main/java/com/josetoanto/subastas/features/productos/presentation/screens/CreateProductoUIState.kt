package com.josetoanto.subastas.features.productos.presentation.screens

data class CreateProductoUIState(
    val nombre: String = "",
    val descripcion: String = "",
    val precioInicial: String = "",
    val imagenUrl: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
