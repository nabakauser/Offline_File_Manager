package com.example.offlinefilemanager.files.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.offlinefilemanager.files.viewmodel.FilesViewModel

@Composable
fun DisplayFilesRoute(
    viewModel: FilesViewModel
) {
    val uiState by viewModel.myFilesUiState.collectAsState()

    DisplayFilesScreen(
        files = uiState.filesList,
        onFileSelected = { viewModel.onFilesSelected(it) }
    )
}