package com.example.baitaptuan3.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baitaptuan3.screen.ComponentDetailScreen
import com.example.baitaptuan3.screen.ComponentListScreen
import com.example.baitaptuan3.screen.IntroScreen

sealed class Screen(val route: String) {
    object Intro : Screen("intro")
    object ComponentList : Screen("component_list")
    object ComponentDetail : Screen("component_detail/{name}") {
        fun createRoute(name: String) = "component_detail/$name"
    }
}

@ExperimentalMaterial3Api
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Intro.route
    ) {
        composable(Screen.Intro.route) {
            IntroScreen(onReadyClick = {
                navController.navigate(Screen.ComponentList.route)
            })
        }
        composable(Screen.ComponentList.route) {
            ComponentListScreen(onItemClick = { componentName ->
                navController.navigate(Screen.ComponentDetail.createRoute(componentName))
            })
        }
        composable(Screen.ComponentDetail.route) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            ComponentDetailScreen(componentName = name)
        }
    }
}
