package com.swoopzi.locationlogger

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat


class LocationForegroundService : Service() {
    private lateinit var locationManager: LocationManager

    private val locationListener: LocationListener = LocationListener {
        saveLocationToFile(it)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

        locationManager.requestLocationUpdates(
            /* provider = */ LocationManager.GPS_PROVIDER,
            /* minTimeMs = */ LOCATION_INTERVAL,
            /* minDistanceM = */ 10f,
            /* listener = */ locationListener
        )
        locationManager.requestLocationUpdates(
            /* provider = */ LocationManager.NETWORK_PROVIDER,
            /* minTimeMs = */ LOCATION_INTERVAL,
            /* minDistanceM = */ 10f,
            /* listener = */ locationListener
        )
        locationManager.requestLocationUpdates(
            /* provider = */ LocationManager.PASSIVE_PROVIDER,
            /* minTimeMs = */ LOCATION_INTERVAL,
            /* minDistanceM = */ 10f,
            /* listener = */ locationListener
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
        locationManager.removeUpdates(locationListener)
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
