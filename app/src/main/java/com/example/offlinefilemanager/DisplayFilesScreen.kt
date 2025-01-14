package com.example.offlinefilemanager

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun DisplayFilesPreview(){
    DisplayFilesScreen()
}

@Composable
fun DisplayFilesScreen() {
    Scaffold(
        topBar = {},
        content = {},
        bottomBar = {

        }
    )
}

@Preview
@Composable
fun ViewFilesPreview(){
    ViewFilesScreen()
}

@Composable
fun ViewFilesScreen(
    modifier: Modifier = Modifier,
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {  }
}

@Preview
@Composable
fun AddFilesPreview() {
    AddFilesButton()
}

@Composable
fun AddFilesButton(){
    Button(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        onClick = {}
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Add Files"
        )
    }
}