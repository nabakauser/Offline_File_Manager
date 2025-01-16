package com.example.offlinefilemanager.utils.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val fileSize: Float,
    val fileType: String,
    val isInTrash: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)