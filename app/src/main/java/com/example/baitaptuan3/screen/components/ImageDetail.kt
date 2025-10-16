package com.example.baitaptuan3.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.baitaptuan3.R

@Composable
fun ImageDetail(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {

        // Ảnh 1 — Tải từ drawable (UTH mặt trước)
        Image(
            painter = painterResource(id = R.drawable.uth_front),
            contentDescription = "UTH main building",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Text("UTH main building")

        Spacer(modifier = Modifier.height(16.dp))

        // Ảnh 2 — Tải từ drawable (UTH campus)
        Image(
            painter = painterResource(id = R.drawable.uth_campus),
            contentDescription = "UTH campus",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Text("UTH campus")
    }
}
