package com.example.offlinefilemanager.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        configKoin()
    }

    private fun configKoin() {
        startKoin {
            androidContext(this@ApplicationClass)
            modules(ConfigurationClass.appModules())
        }
    }
}