package com.example.offlinefilemanager.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

class FileManager(private val context: Context) {
    private fun getAppSpecificExternalStorageDir(): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (dir != null && !dir.exists()) {
            dir.mkdirs()
        }
        return dir ?: throw IllegalStateException("External storage directory not available")
    }

    fun saveFileToAppStorage(uri: Uri): String? {
        val contentResolver = context.contentResolver
        val destinationDir = getAppSpecificExternalStorageDir()
        val fileName = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"
        val destinationFile = File(destinationDir, fileName)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllFilesInAppStorage(): List<File> {
        val dir = getAppSpecificExternalStorageDir()
        return dir.listFiles()?.toList() ?: emptyList()
    }
}