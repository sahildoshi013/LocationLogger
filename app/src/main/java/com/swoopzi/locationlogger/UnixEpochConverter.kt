package com.swoopzi.locationlogger

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object UnixEpochConverter {
    fun convertUnixEpochToReadable(unixEpochTime: Long): String? {
        return try {
            // Create a SimpleDateFormat object with your desired date format
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Convert Unix epoch time to milliseconds
            val date = Date(unixEpochTime)

            // Format the date as a readable string
            sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
