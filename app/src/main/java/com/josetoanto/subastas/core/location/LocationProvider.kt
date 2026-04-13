package com.josetoanto.subastas.core.location

interface LocationProvider {
    suspend fun getCurrentLocation(): Result<LatLon>
}

data class LatLon(val lat: Double, val lon: Double)
