package com.swoopzi.locationlogger

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    // Create an ActivityResultLauncher for location permission request
    private val requestLocationPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, you can proceed with location-related tasks
                startLocationService()
            } else {
                // Permission denied, handle this situation (e.g., show a message to the user)
                Toast.makeText(this, "Log will not generate", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.startTrackingButton).setOnClickListener {
            startLocationService()
        }
        findViewById<Button>(R.id.stopTrackingButton).setOnClickListener {
            stopLocationService()
        }
    }

    private fun stopLocationService() {
        stopService(
            Intent(
                this,
                LocationForegroundService::class.java
            )
        )

    }

    private fun startLocationService() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            val serviceIntent = Intent(
                this,
                LocationForegroundService::class.java
            )
            ContextCompat.startForegroundService(
                this,
                serviceIntent
            )
        }
    }
}