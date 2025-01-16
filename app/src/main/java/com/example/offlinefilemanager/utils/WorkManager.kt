package com.example.offlinefilemanager.utils

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.util.concurrent.TimeUnit

class FileDeletionWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val filePath = inputData.getString("FILE_PATH")
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                val isDeleted = file.delete()
                return if (isDeleted) Result.success()
                else Result.failure()
            }
        }
        return Result.failure()
    }

    companion object {
        fun createDeleteWorkRequest(filePath: String): OneTimeWorkRequest {
            val inputData = workDataOf("FILE_PATH" to filePath)
            return OneTimeWorkRequestBuilder<FileDeletionWorker>()
                .setInputData(inputData)
                .setInitialDelay(24, TimeUnit.HOURS)
                .build()
        }
    }
}