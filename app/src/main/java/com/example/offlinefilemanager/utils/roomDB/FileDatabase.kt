package com.example.offlinefilemanager.utils.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FileModelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FileDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
}