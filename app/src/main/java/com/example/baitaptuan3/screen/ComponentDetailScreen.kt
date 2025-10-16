package com.example.baitaptuan3.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.baitaptuan3.screen.components.*
@ExperimentalMaterial3Api
@Composable
fun ComponentDetailScreen(componentName: String) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Components Detail") })
        }
    ) { padding ->
        when (componentName) {
            "Text" -> TextDetail(modifier = Modifier.padding(padding))
            "Image" -> ImageDetail(modifier = Modifier.padding(padding))
            "TextField" -> TextFieldDetail(modifier = Modifier.padding(padding))
            "PasswordField" -> PasswordFieldDetail(modifier = Modifier.padding(padding))
            "Column" -> ColumnDetail(modifier = Modifier.padding(padding))
            "Row" -> RowDetail(modifier = Modifier.padding(padding))
            else -> Text("Unknown component")
        }
    }
}
