package com.josetoanto.subastas.features.productos.data.datasources.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object AnalyticsDtos {

    @Serializable
    data class PriceHistoryEntryDto(
        @SerialName("fecha") val fecha: String = "",
        @SerialName("precio") val precio: Double = 0.0,
        @SerialName("postor") val postor: String? = null
    )

    @Serializable
    data class ProductoAnalyticsDto(
        @SerialName("producto_id") val productoId: Int,
        @SerialName("nombre") val nombre: String,
        @SerialName("historial_precios") val historialPrecios: List<PriceHistoryEntryDto> = emptyList(),
        @SerialName("total_postores") val totalPostores: Int = 0,
        @SerialName("puja_inicial") val pujaInicial: String = "0.0",
        @SerialName("puja_maxima") val pujaMaxima: Double? = null,
        @SerialName("puja_minima") val pujaMinima: Double? = null
    )
}
