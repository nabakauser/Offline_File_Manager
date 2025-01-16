package com.example.offlinefilemanager.offlineFiles.view.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.offlinefilemanager.offlineFiles.view.trash.TrashScreen
import com.example.offlinefilemanager.offlineFiles.viewmodel.ScreenType
import com.example.offlinefilemanager.offlineFiles.viewmodel.FilesViewModel

@Composable
fun DisplayFilesRoute(
    viewModel: FilesViewModel
) {
    val uiState by viewModel.myFilesUiState.collectAsState()

    when(uiState.screenType) {
        ScreenType.HOME -> {
            DisplayFilesScreen(
                files = uiState.filesList,
                onTrashClicked = { viewModel.onOpenTrashScreen() },
                onFileSelected = { viewModel.onFilesSelected(it) },
                onMoveToTrashClicked = { viewModel.onAddToTrashClicked(it) },
                onFileDownload = { viewModel.onFileDownload(it) }
            )
        }
        ScreenType.TRASH -> {
            TrashScreen(
                trashItems = uiState.trashFiles,
                onBackClicked = { viewModel.onOpenHomeScreen() },
                onRestoreItem = {  viewModel.onRestoreFileClicked(it) },
                onDeleteItem = { viewModel.onFileDeleted(it) }
            )
        }
    }

}