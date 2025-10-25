package com.example.uthsmarttasks.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uthsmarttasks.ui.auth.ConfirmScreen
import com.example.uthsmarttasks.ui.auth.ForgotPasswordScreen
import com.example.uthsmarttasks.ui.auth.LoginScreen
import com.example.uthsmarttasks.ui.auth.ResetPasswordScreen
import com.example.uthsmarttasks.ui.auth.VerifyCodeScreen
import com.example.uthsmarttasks.ui.onboarding.OnBoardingScreen
import com.example.uthsmarttasks.ui.onboarding.SkipTransitionScreen
import com.example.uthsmarttasks.ui.onboarding.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("onboarding") { OnBoardingScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("verify_code") { VerifyCodeScreen(navController) }
        composable("reset_password") { ResetPasswordScreen(navController) }
        composable("confirm") { ConfirmScreen(navController) }
        composable("splash") { SplashScreen(navController) }
        composable("onboarding") { OnBoardingScreen(navController) }
        composable("login") { LoginScreen(navController) }  // 👈 thêm dòng này
        composable("home") { /* nếu cần sau login */ }
        composable("skip_transition") { SkipTransitionScreen(navController) }
        composable("home") {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Welcome to UTH SmartTasks!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

            }
        }
    }
}
