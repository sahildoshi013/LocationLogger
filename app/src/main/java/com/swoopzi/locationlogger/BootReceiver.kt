package com.swoopzi.locationlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // The device has finished booting, start your service here
            val serviceIntent = Intent(
                context,
                LocationForegroundService::class.java
            )
            ContextCompat.startForegroundService(
                context,
                serviceIntent
            )
        }
    }
}
