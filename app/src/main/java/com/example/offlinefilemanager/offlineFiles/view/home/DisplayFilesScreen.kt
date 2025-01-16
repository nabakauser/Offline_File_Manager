package com.example.offlinefilemanager.offlineFiles.view.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.offlinefilemanager.offlineFiles.viewmodel.FileModel
import com.example.offlinefilemanager.offlineFiles.viewmodel.FileType
import com.example.offlinefilemanager.ui.theme.Violet

@Preview
@Composable
fun DisplayFilesPreview() {
    DisplayFilesScreen(
        files = listOf(FileModel(), FileModel(), FileModel()),
        onTrashClicked = {},
        onFileSelected = {},
        onMoveToTrashClicked = {},
        onFileDownload = {},
    )
}

@Composable
fun DisplayFilesScreen(
    files: List<FileModel>,
    onTrashClicked: () -> Unit,
    onFileSelected: (Uri?) -> Unit,
    onMoveToTrashClicked: (FileModel) -> Unit,
    onFileDownload: (FileModel) -> Unit,
) {
    Scaffold(
        topBar = {
            FilesTopBar(
                onTrashClicked = onTrashClicked,
            )
        },
        content = {
            var filterType by remember { mutableStateOf(FileType.ALL) }
            val filteredFiles = when (filterType) {
                FileType.IMAGE -> files.filter { it.fileType?.contains("image", ignoreCase = true) == true }
                FileType.AUDIO -> files.filter { it.fileType?.contains("audio", ignoreCase = true) == true }
                FileType.VIDEO -> files.filter { it.fileType?.contains("video", ignoreCase = true) == true }
                FileType.OTHER -> files.filter { it.fileType?.contains("image", ignoreCase = true) == false &&
                        !it.fileType.contains("audio", ignoreCase = true) &&
                        !it.fileType.contains("video", ignoreCase = true) }
                else -> files
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                FilterChipRow(filterType = filterType, onFilterSelected = { selectedFilter ->
                    filterType = selectedFilter
                })
                ViewFilesScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                    filesList = filteredFiles,
                    onFileDeleted = onMoveToTrashClicked,
                    onFileDownload = onFileDownload,
                )
            }
        },
        floatingActionButton = {
            AddFilesButton(
                onFileSelected = onFileSelected
            )
        },
    )
}

@Composable
fun FilterChipRow(
    filterType: FileType,
    onFilterSelected: (FileType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = "All",
            selected = filterType == FileType.ALL,
            onClick = { onFilterSelected(FileType.ALL) }
        )
        FilterChip(
            label = "Images",
            selected = filterType == FileType.IMAGE,
            onClick = { onFilterSelected(FileType.IMAGE) }
        )
        FilterChip(
            label = "Audio",
            selected = filterType == FileType.AUDIO,
            onClick = { onFilterSelected(FileType.AUDIO) }
        )
        FilterChip(
            label = "Videos",
            selected = filterType == FileType.VIDEO,
            onClick = { onFilterSelected(FileType.VIDEO) }
        )
        FilterChip(
            label = "Other",
            selected = filterType == FileType.OTHER,
            onClick = { onFilterSelected(FileType.OTHER) }
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(label)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@Preview
@Composable
fun FilesTopBarPreview() {
    FilesTopBar(
        onTrashClicked = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesTopBar(
    onTrashClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Violet,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            title = {
                Text(
                    text = "Offline File App",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(
                    onClick = onTrashClicked
                ) {
                    Icon(Icons.Default.Delete,"")
                }
            }
        )
    }
}

@Composable
fun ViewFilesScreen(
    modifier: Modifier = Modifier,
    filesList: List<FileModel> = arrayListOf(),
    onFileDeleted: (FileModel) -> Unit,
    onFileDownload: (FileModel) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filesList) { file ->
            FileItem(
                file = file,
                onFileDeleted = onFileDeleted,
                onFileDownloaded = onFileDownload,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FileItemPreview() {
    FileItem(
        file = FileModel(
            fileName = "test task",
            filePath = "/Internal storage/Download/test task.pdf",
            fileSize = 21.48f,
            fileType = "image/jpp",
        ),
        onFileDeleted = {},
        onFileDownloaded = {}
    )
}

@Composable
fun FileItem(
    file: FileModel,
    onFileDeleted: (FileModel) -> Unit,
    onFileDownloaded: (FileModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (file.fileType?.contains("image") == true) {
            val bitmap = BitmapFactory.decodeFile(file.filePath)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                IconPlaceholder(
                    icon = Icons.Filled.Image,
                    label = "Image",
                )
            }
        } else {
            IconPlaceholder(
                icon = getFileTypeIcon(file.fileType),
                label = file.fileType ?: "File",
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = file.fileName ?: "Unknown",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${file.fileSize ?: 0} bytes",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }

        var expanded by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Options",tint = Violet)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                MenuOption(
                    icon = Icons.Default.Download,
                    text = "Download",
                    onMenuClicked = {
                        expanded = false
                        onFileDownloaded(file)
                    }
                )
                HorizontalDivider(color = Violet)
                MenuOption(
                    icon = Icons.Default.Delete,
                    text = "Move to Trash",
                    onMenuClicked = {
                        expanded = false
                        onFileDeleted(file)
                    }
                )
            }
        }
    }
}

@Composable
fun MenuOption(
    icon: ImageVector,
    text: String,
    onMenuClicked: () -> Unit,
){
    DropdownMenuItem(
        leadingIcon = {
            Icon(icon, "", tint = Violet)
        },
        text = {
            Text(
                text = text,
                color = Violet,
                fontWeight = FontWeight.SemiBold,
            ) },
        onClick = {
            onMenuClicked()
        }
    )
}



@Composable
fun IconPlaceholder(icon: ImageVector, label: String) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        modifier = Modifier
            .padding(4.dp)
            .size(48.dp),
        tint = Violet
    )
}

fun getFileTypeIcon(fileType: String?): ImageVector {
    return when {
        fileType?.contains("video") == true -> Icons.Filled.VideoFile
        fileType?.contains("audio") == true -> Icons.Default.AudioFile
        else -> Icons.Default.Description
    }
}

@Preview
@Composable
fun AddFilesPreview() {
    AddFilesButton(
        onFileSelected = {}
    )
}

@Composable
fun AddFilesButton(
    onFileSelected: (Uri?) -> Unit,
) {
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                Log.d("filesLog", "Selected URI: $uri")
                onFileSelected(uri)
            }
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.all { it.value }) {
                pickFileLauncher.launch(arrayOf("*/*"))
            } else {
                Toast.makeText(context, "Some permissions were denied.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun checkAndRequestPermissions() {
        val requiredPermissions = mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.READ_MEDIA_AUDIO)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                    return
                }
            }
        }

        val notGrantedPermissions = requiredPermissions.filter { permission ->
            checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isEmpty()) {
            pickFileLauncher.launch(arrayOf("*/*"))
        } else {
            requestPermissionLauncher.launch(notGrantedPermissions.toTypedArray())
        }
    }

    FloatingActionButton(
        onClick = {
            checkAndRequestPermissions()
        },
        containerColor = Violet,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}
