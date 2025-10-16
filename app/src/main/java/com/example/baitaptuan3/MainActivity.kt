package com.example.baitaptuan3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.baitaptuan3.navigation.NavGraph
import com.example.baitaptuan3.ui.theme.BaiTapTuan3Theme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BaiTapTuan3Theme {
                NavGraph()
            }
        }
    }
}
