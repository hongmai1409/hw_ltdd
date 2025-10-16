package com.example.baitaptuan3.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun ComponentListScreen(onItemClick: (String) -> Unit) {
    val components = listOf(
        "Text" to "Displays text",
        "Image" to "Displays an image",
        "TextField" to "Input field for text",
        "PasswordField" to "Input field for passwords",
        "Column" to "Arranges elements vertically",
        "Row" to "Arranges elements horizontally"
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("UI Components List") })
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            components.forEach { (title, desc) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onItemClick(title) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text(desc, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
