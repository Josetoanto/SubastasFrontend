package com.josetoanto.subastas.core.location

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): Result<LatLon> = runCatching {
        val location = fusedClient.lastLocation.await()
            ?: error("Ubicación no disponible. Activa el GPS y vuelve a intentarlo.")
        LatLon(location.latitude, location.longitude)
    }
}
