package com.example.offlinefilemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.offlinefilemanager.files.view.DisplayFilesRoute
import com.example.offlinefilemanager.files.viewmodel.FilesViewModel
import com.example.offlinefilemanager.ui.theme.OfflineFileManagerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FilesViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OfflineFileManagerTheme {
                DisplayFilesRoute(
                    viewModel = viewModel
                )
            }
        }
    }
}