package com.example.uthsmarttasks.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uthsmarttasks.R

@Composable
fun VerifyCodeScreen(navController: NavController) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF0A4DA2))
        }

        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.uth),
            contentDescription = null,
            modifier = Modifier.height(90.dp)
        )

        Text(
            text = "SmartTasks",
            color = Color(0xFF0A4DA2),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(25.dp))

        Text("Verify Code", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(Modifier.height(10.dp))

        Text("Enter the code we sent you via your registered Email", color = Color.Gray, fontSize = 15.sp)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            placeholder = { Text("123456") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(25.dp))

        Button(
            onClick = { navController.navigate("reset_password") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A4DA2)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Next", color = Color.White, fontSize = 18.sp)
        }
    }
}
