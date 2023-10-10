package com.swoopzi.locationlogger

import android.content.Context
import android.location.Location
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


object CSVWriter {

    private const val fileName = "LocationLog.csv"

    fun writeCSV(
        context: Context?,
        location: Location
    ): Boolean {
        // Check if external storage is available

        return try {
            val directory = File(context!!.getExternalFilesDir(null), "CSV")
            if (!directory.exists()) {
                directory.mkdir()
            }
            val csvFile = File(directory, fileName)
            val fos = FileOutputStream(csvFile, true)
            val writer = OutputStreamWriter(fos)

            writer.append(UnixEpochConverter.convertUnixEpochToReadable(location.time))
            writer.append(",")
            writer.append(location.latitude.toString())
            writer.append(",")
            writer.append(location.longitude.toString())
            writer.append(",")
            writer.append(location.provider)
            writer.append(",")
            writer.append(location.accuracy.toString())
            writer.append(",")
            writer.append("\n")

            writer.flush()
            writer.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
