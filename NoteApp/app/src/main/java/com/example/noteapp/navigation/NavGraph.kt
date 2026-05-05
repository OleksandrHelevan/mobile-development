package com.example.noteapp.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import com.example.noteapp.di.ServiceLocator
import com.example.noteapp.ui.onboarding.OnboardingScreen
import com.example.noteapp.ui.onboarding.InputNameScreen
import com.example.noteapp.ui.main.MainScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass
) {
    val context = LocalContext.current
    val settingsRepository = ServiceLocator.settingsRepository(context)
    val settings by settingsRepository.settingsFlow.collectAsState(initial = null)
    val currentSettings = settings

    if (currentSettings == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (currentSettings.userName.isBlank()) {
        "onboarding"
    } else {
        "main/${Uri.encode(currentSettings.userName)}"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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
            val userName = backStackEntry.arguments?.getString("userName")?.let(Uri::decode) ?: "Гість"

            MainScreen(
                userName = userName,
                widthSizeClass = widthSizeClass
            )
        }
    }
}