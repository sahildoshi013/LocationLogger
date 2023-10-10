package com.swoopzi.locationlogger

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationForegroundService : Service() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        LOCATION_INTERVAL
    ).setMinUpdateDistanceMeters(10F).build()
    private val locationCallback = object : LocationCallback() {

        override fun onLocationAvailability(p0: LocationAvailability) {
            Log.d(TAG, "onLocationAvailability() called with: p0 = $p0")
            super.onLocationAvailability(p0)
        }

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                saveLocationToFile(it)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return START_NOT_STICKY
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        // Start the service in the foreground
        return START_STICKY
    }

    private fun saveLocationToFile(location: Location) {
        // Implement code to save location data to a file (e.g., CSV or JSON).
        Log.d(TAG, "Location: ${location.latitude}, ${location.longitude}, ${location.provider}, ${location.accuracy}")
        val request = CSVWriter.writeCSV(this, location)
        Log.d(TAG, "saveLocationToFile: $request")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
        // Stop location updates when the service is destroyed.
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Collecting location data")
            .setSmallIcon(R.mipmap.ic_launcher)
        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        private const val TAG = "LocationForegroundServi"
        private const val LOCATION_INTERVAL = (5 * 1000).toLong()
        private const val CHANNEL_ID = "LocationForegroundServiceChannel"
    }
}
