package com.example.offlinefilemanager.files.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.offlinefilemanager.utils.FileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FilesViewModel(
    private val fileManager: FileManager
): ViewModel() {
    private val myFilesViewModelState = MutableStateFlow(MyFilesModel())

    val myFilesUiState = myFilesViewModelState.asStateFlow()

    fun onFilesSelected(uri: Uri?) {
        val test = saveFile(uri = uri ?: Uri.EMPTY)
        val all = fileManager.getAllFilesInAppStorage()
        Log.e("filesLog","$test")
        Log.e("filesLog","$all")
        val newFile = FileModel(
            fileName = uri?.lastPathSegment,
            filePath = uri?.path
        )
        val updatedFiles = ArrayList(myFilesViewModelState.value.filesList).apply { add(newFile) }
        myFilesViewModelState.update {
            it.copy(
                filesList = updatedFiles,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    private fun saveFile(uri: Uri): String? {
        return fileManager.saveFileToAppStorage(uri)
    }
}

data class MyFilesModel(
    val lastUpdated: Long = System.currentTimeMillis(),
    val filesList: List<FileModel> = arrayListOf(),
)

data class FileModel(
    val fileName: String? = null,
    val fileSize: Int? = null,
    val filePath: String? = null,
    val fileType: String? = null,
)