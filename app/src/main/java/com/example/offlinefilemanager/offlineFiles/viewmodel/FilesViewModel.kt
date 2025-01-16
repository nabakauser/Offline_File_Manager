package com.example.offlinefilemanager.offlineFiles.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.offlinefilemanager.utils.FileDeletionWorker
import com.example.offlinefilemanager.utils.FileManager
import com.example.offlinefilemanager.utils.roomDB.FileDao
import com.example.offlinefilemanager.utils.roomDB.FileModelEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class FilesViewModel(
    private val fileManager: FileManager,
    private val workManager: WorkManager,
    private val filesDao: FileDao,
): ViewModel() {
    private val myFilesViewModelState = MutableStateFlow(MyFilesModel(screenType = ScreenType.HOME))

    val myFilesUiState = myFilesViewModelState.asStateFlow()

    init {
        getFileFromStorage()
    }

    private fun getFileFromStorage() {
        viewModelScope.launch {
            val homeFiles = filesDao.getHomeFiles()
            val trashFiles = filesDao.getTrashFiles()
            val currentTime = System.currentTimeMillis()
            trashFiles.forEach { file ->
                if (currentTime - file.timestamp >= 86400000) {
                    deleteFile(file)
                }
            }
            myFilesViewModelState.update {
                it.copy(
                    filesList = homeFiles.map { file ->
                        FileModel(
                            fileName = file.fileName,
                            filePath = file.filePath,
                            fileSize = file.fileSize,
                            fileType = file.fileType
                        )
                    }.toCollection(ArrayList()),
                    trashFiles = trashFiles.map { file ->
                        FileModel(
                            fileName = file.fileName,
                            filePath = file.filePath,
                            fileSize = file.fileSize,
                            fileType = file.fileType
                        )
                    }.toCollection(ArrayList()),
                    lastUpdated = System.currentTimeMillis()
                )
            }
        }
    }

    fun onFilesSelected(uri: Uri?) {
        val filePath = saveFile(uri = uri ?: Uri.EMPTY)
        if (filePath != null) {
            val file = File(filePath)
            val fileEntity = FileModelEntity(
                fileName = file.name,
                filePath = file.absolutePath,
                fileSize = file.length().toFloat(),
                fileType = fileManager.getMimeType(file) ?: "",
                isInTrash = false
            )
            viewModelScope.launch {
                insertFileToDatabase(fileEntity)
                updateHomeFilesUI(fileEntity)
            }
        }
    }

    private fun updateHomeFilesUI(fileEntity: FileModelEntity) {
        val newFileModel = FileModel(
            fileName = fileEntity.fileName,
            filePath = fileEntity.filePath,
            fileSize = fileEntity.fileSize,
            fileType = fileEntity.fileType
        )
        myFilesViewModelState.update { state ->
            val updatedFilesList = (state.filesList + newFileModel).toCollection(ArrayList())
            state.copy(
                filesList = updatedFilesList,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    private fun saveFile(uri: Uri): String? {
        return fileManager.saveFileToAppStorage(uri)
    }

    private fun insertFileToDatabase(fileEntity: FileModelEntity) {
        viewModelScope.launch {
            try {
                filesDao.insertFile(fileEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onFileDeleted(fileModel: FileModel) {
        viewModelScope.launch {
            try {
                val file = File(fileModel.filePath ?: "")
                if (file.exists()) {
                    file.delete()
                }
                Log.d("filesLog","vmp -- ${fileModel.filePath}")
                filesDao.deleteFile(fileModel.filePath ?: "")

                myFilesViewModelState.update { state ->
                    val updatedTrashFiles = state.trashFiles.filter { it != fileModel }.toCollection(ArrayList())
                    state.copy(
                        trashFiles = updatedTrashFiles,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteFile(fileModelEntity: FileModelEntity) {
        viewModelScope.launch {
            try {
                val file = File(fileModelEntity.filePath)
                if (file.exists()) {
                    file.delete()
                }

                filesDao.deleteFile(fileModelEntity.filePath)

                val fileModel = FileModel(
                    fileName = fileModelEntity.fileName,
                    filePath = fileModelEntity.filePath,
                    fileSize = fileModelEntity.fileSize,
                    fileType = fileModelEntity.fileType
                )

                myFilesViewModelState.update { state ->
                    val updatedTrashFiles = state.trashFiles.filter { it != fileModel }.toCollection(ArrayList())
                    state.copy(
                        trashFiles = updatedTrashFiles,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun onFileDownload(fileModel: FileModel) {
        Log.d("filesLog","dl - $fileModel")
        try {
            fileManager.copyToDownloads(
                filePath = fileModel.filePath ?: "",
                fileName = fileModel.fileName ?: "",
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onOpenTrashScreen() {
        myFilesViewModelState.update {
            it.copy(
                screenType = ScreenType.TRASH,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    fun onOpenHomeScreen() {
        myFilesViewModelState.update {
            it.copy(
                screenType = ScreenType.HOME,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }

    fun onAddToTrashClicked(fileModel: FileModel) {
        viewModelScope.launch {
            filesDao.updateFileTrashState(fileModel.filePath ?: "", true)
            myFilesViewModelState.update { state ->
                val updatedFilesList =
                    state.filesList.filter { it != fileModel }.toCollection(ArrayList())
                val updatedTrashFiles = (state.trashFiles + fileModel).toCollection(ArrayList())
                state.copy(
                    filesList = updatedFilesList,
                    trashFiles = updatedTrashFiles
                )
            }
            scheduleFileDeletion(fileModel)
        }
    }

    fun onRestoreFileClicked(fileModel: FileModel) {
        viewModelScope.launch {
            filesDao.updateFileTrashState(fileModel.filePath ?: "", false)
            myFilesViewModelState.update { state ->
                val updatedTrashFiles = state.trashFiles.filter { it != fileModel }.toCollection(ArrayList())
                val updatedHomeFiles = (state.filesList + fileModel).toCollection(ArrayList())
                state.copy(
                    filesList = updatedHomeFiles,
                    trashFiles = updatedTrashFiles
                )
            }
        }

    }

    private fun scheduleFileDeletion(fileModel: FileModel) {
        viewModelScope.launch {
            val deleteWorkRequest = FileDeletionWorker.createDeleteWorkRequest(fileModel.filePath ?: "")
            workManager.enqueue(deleteWorkRequest)

            workManager.getWorkInfoByIdLiveData(deleteWorkRequest.id).observeForever { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    if (workInfo.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                        viewModelScope.launch {
                            onFileDeleted(fileModel)
                        }
                    } else {
                        Log.e("FileDeletion", "File deletion failed for: ${fileModel.filePath}")
                    }
                }
            }
        }
    }
}

data class MyFilesModel(
    val lastUpdated: Long = System.currentTimeMillis(),
    val screenType: ScreenType = ScreenType.HOME,
    val filesList: MutableList<FileModel> = arrayListOf(),
    val trashFiles: MutableList<FileModel> = arrayListOf(),
)

data class FileModel(
    val fileName: String? = null,
    val fileSize: Float? = null,
    val filePath: String? = null,
    val fileType: String? = null,
)

enum class ScreenType {
    HOME,
    TRASH
}

enum class FileType {
    ALL, IMAGE, AUDIO, VIDEO, OTHER
}