package com.example.noteapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.noteapp.ui.onboarding.OnboardingScreen
import com.example.noteapp.ui.onboarding.InputNameScreen
import com.example.noteapp.ui.main.MainScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable("input_name") {
            InputNameScreen(navController)
        }
        composable(
            route = "main/{userName}",
            arguments = listOf(navArgument("userName") { defaultValue = "Гість" })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Гість"
            MainScreen(userName)
        }
    }
}