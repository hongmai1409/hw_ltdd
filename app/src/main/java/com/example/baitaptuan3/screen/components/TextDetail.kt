package com.example.baitaptuan3.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextDetail(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                    append("The quick ")
                }
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                    append("Brown ")
                }
                append("fox j")
                withStyle(SpanStyle(fontSize = 22.sp)) { append("u") }
                append("mps ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("over ") }
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("the ") }
                append("lazy dog.")
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
