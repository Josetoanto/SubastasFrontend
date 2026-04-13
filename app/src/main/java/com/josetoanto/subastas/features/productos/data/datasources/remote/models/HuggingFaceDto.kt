package com.josetoanto.subastas.features.productos.data.datasources.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class HfZeroShotRequest(
    val inputs: String,
    val parameters: HfZeroShotParameters
)

@Serializable
data class HfZeroShotParameters(
    val candidate_labels: List<String>
)

@Serializable
data class HfZeroShotResponse(
    val sequence: String? = null,
    val labels: List<String> = emptyList(),
    val scores: List<Double> = emptyList(),
    val error: String? = null
)
