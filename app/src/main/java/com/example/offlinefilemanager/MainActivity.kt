package com.example.offlinefilemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.offlinefilemanager.ui.theme.OfflineFileManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OfflineFileManagerTheme {
                DisplayFilesScreen()
            }
        }
    }
}