package com.example.offlinefilemanager.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
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

        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else {
                null
            }
        } ?: "file_${System.currentTimeMillis()}"

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


    /*fun getAllFilesInAppStorage(): List<File> {
        val dir = getAppSpecificExternalStorageDir()
        return dir.listFiles()?.toList() ?: emptyList()
    }*/

    fun getMimeType(file: File): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toString())
        return if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        } else {
            null
        }
    }

    fun copyToDownloads(filePath: String, fileName: String): Boolean {
        return try {
            val sourceFile = File(filePath)
            val destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destinationFile = File(destinationDir, fileName)

            sourceFile.copyTo(destinationFile, overwrite = true)
            Toast.makeText(context, "Downloaded Successfully",Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}