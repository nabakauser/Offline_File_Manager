package com.example.offlinefilemanager.di

import com.example.offlinefilemanager.files.viewmodel.FilesViewModel
import com.example.offlinefilemanager.utils.FileManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


object ConfigurationClass {
    private val viewModelModules = module {
        viewModel { FilesViewModel(get()) }
    }

    private val commonModules = module {
        single { FileManager(androidContext()) }
    }

    fun appModules() = viewModelModules + commonModules
}