package com.example.offlinefilemanager.offlineFiles.view.trash

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.offlinefilemanager.offlineFiles.view.home.IconPlaceholder
import com.example.offlinefilemanager.offlineFiles.view.home.MenuOption
import com.example.offlinefilemanager.offlineFiles.view.home.getFileTypeIcon
import com.example.offlinefilemanager.offlineFiles.viewmodel.FileModel
import com.example.offlinefilemanager.ui.theme.Violet

@Preview
@Composable
fun TrashScreenPreview() {
    TrashScreen(
        trashItems = listOf(FileModel(), FileModel(), FileModel()),
        onBackClicked = {},
        onRestoreItem = {},
        onDeleteItem = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    trashItems: List<FileModel>,
    onBackClicked: () -> Unit,
    onRestoreItem: (FileModel) -> Unit,
    onDeleteItem: (FileModel) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Violet,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClicked() }
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew,"", tint = Color.White)
                    }
                },
                title = {
                    Text(
                        text = "Trash",
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info Icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "Items in the Trash will be permanently deleted after 24hrs.",
                        color = Color.Gray
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(trashItems) { file ->
                        TrashItem(
                            file = file,
                            onFileRestored = onRestoreItem,
                            onFileDeleted = onDeleteItem,
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun TrashItem(
    file: FileModel,
    onFileRestored: (FileModel) -> Unit,
    onFileDeleted: (FileModel) -> Unit,
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
                    icon = Icons.Default.Restore,
                    text = "Restore",
                    onMenuClicked = {
                        expanded = false
                        onFileRestored(file)
                    }
                )
                HorizontalDivider(color = Violet)
                MenuOption(
                    icon = Icons.Default.Delete,
                    text = "Delete Permanently",
                    onMenuClicked = {
                        expanded = false
                        onFileDeleted(file)
                    }
                )
            }
        }
    }
}