package com.example.offlinefilemanager.files.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.offlinefilemanager.files.viewmodel.FileModel

@Preview
@Composable
fun DisplayFilesPreview() {
    DisplayFilesScreen(
        files = listOf(),
        onFileSelected = {}
    )
}

@Composable
fun DisplayFilesScreen(
    files: List<FileModel>,
    onFileSelected: (Uri?) -> Unit
) {
    Scaffold(
        topBar = {},
        content = {
            ViewFilesScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                filesList = files
            )
        },
        bottomBar = {
            AddFilesButton(
                onFileSelected = onFileSelected
            )
        }
    )
}

@Preview
@Composable
fun ViewFilesPreview() {
    ViewFilesScreen()
}

@Composable
fun ViewFilesScreen(
    modifier: Modifier = Modifier,
    filesList: List<FileModel> = arrayListOf()
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn {
            items(filesList) { files ->
                Column {
                    Text(
                        text = files.fileName ?: "No Path found"
                    )
                    Text(
                        text = files.filePath ?: "No Path found"
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
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

    Button(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            checkAndRequestPermissions()
        }
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Add Files"
        )
    }
}
