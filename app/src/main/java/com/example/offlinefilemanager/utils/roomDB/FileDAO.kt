package com.example.offlinefilemanager.utils.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileModelEntity)

    @Query("SELECT * FROM files WHERE isInTrash = 0")
    suspend fun getHomeFiles(): List<FileModelEntity>

    @Query("SELECT * FROM files WHERE isInTrash = 1")
    suspend fun getTrashFiles(): List<FileModelEntity>

    @Query("DELETE FROM files WHERE filePath = :filePath")
    suspend fun deleteFile(filePath: String)

    @Query("UPDATE files SET isInTrash = :isInTrash WHERE filePath = :filePath")
    suspend fun updateFileTrashState(filePath: String, isInTrash: Boolean)
}