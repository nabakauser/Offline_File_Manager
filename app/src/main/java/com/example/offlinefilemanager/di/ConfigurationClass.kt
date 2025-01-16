package com.example.offlinefilemanager.di

import androidx.room.Room
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.offlinefilemanager.offlineFiles.viewmodel.FilesViewModel
import com.example.offlinefilemanager.utils.FileDeletionWorker
import com.example.offlinefilemanager.utils.FileManager
import com.example.offlinefilemanager.utils.roomDB.FileDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


object ConfigurationClass {
    private val viewModelModules = module {
        viewModel { FilesViewModel(get(),get(), get()) }
    }

    private val commonModules = module {
        single { FileManager(androidContext()) }
        factory { (workerParams: WorkerParameters) -> FileDeletionWorker(androidContext(), workerParams) }
        single { WorkManager.getInstance(androidContext()) }
        single {
            Room.databaseBuilder(
                androidContext(),
                FileDatabase::class.java,
                "fileDatabase"
            ).fallbackToDestructiveMigration().build()
        }
        single { get<FileDatabase>().fileDao() }
    }

    fun appModules() = viewModelModules + commonModules
}