package com.example.offlinefilemanager

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Preview
@Composable
fun DisplayFilesPreview() {
    DisplayFilesScreen()
}

@Composable
fun DisplayFilesScreen() {
    Scaffold(
        topBar = {},
        content = {
            ViewFilesScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        },
        bottomBar = {
            AddFilesButton()
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
) {
    Column(
        modifier = modifier
    ) { }
}

@Preview
@Composable
fun AddFilesPreview() {
    AddFilesButton()
}

@Composable
fun AddFilesButton() {
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                Log.d("filesLog", "Selected URI: $uri")
            }
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.all { it.value }) {
                pickFileLauncher.launch(arrayOf("image/*"))
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
            pickFileLauncher.launch(arrayOf("image/*"))
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
