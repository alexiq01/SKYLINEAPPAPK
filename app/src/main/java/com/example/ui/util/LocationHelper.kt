package com.example.ui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

data class GpsCoordinates(
    val latitude: Double,
    val longitude: Double,
    val provider: String = "gps"
)

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    suspend fun getLastKnownOrCurrentLocation(context: Context): GpsCoordinates? {
        if (!hasLocationPermission(context)) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        // 1. Try last known locations
        try {
            val gpsLocation = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else null

            val networkLocation = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } else null

            val passiveLocation = if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            } else null

            val bestLast = gpsLocation ?: networkLocation ?: passiveLocation
            if (bestLast != null) {
                return GpsCoordinates(
                    latitude = bestLast.latitude,
                    longitude = bestLast.longitude,
                    provider = bestLast.provider ?: "last_known"
                )
            }
        } catch (_: SecurityException) {
            return null
        } catch (_: Exception) {}

        // 2. Request single fresh location with a fast timeout (3 seconds)
        return withTimeoutOrNull(3000) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        if (continuation.isActive) {
                            continuation.resume(
                                GpsCoordinates(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    provider = location.provider ?: "live"
                                )
                            )
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                try {
                    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    val providerToUse = when {
                        isGpsEnabled -> LocationManager.GPS_PROVIDER
                        isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
                        else -> null
                    }

                    if (providerToUse != null) {
                        locationManager.requestSingleUpdate(providerToUse, listener, Looper.getMainLooper())
                    } else {
                        if (continuation.isActive) continuation.resume(null)
                    }
                } catch (_: Exception) {
                    if (continuation.isActive) continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (_: Exception) {}
                }
            }
        }
    }
}
