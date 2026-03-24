// ui/onboarding/OnboardingScreen.kt
package com.example.noteapp.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OnboardingScreen(navController: NavController) {
    // Отримуємо результат (ім'я) з екрану введення через SavedStateHandle
    val savedName = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("userName")
        ?.observeAsState()

    val userName = savedName?.value ?: ""

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вітаємо у NoteApp", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate("input_name") }) {
            Text("Ввести ім'я")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Кнопка активна тільки якщо введене ім'я
        Button(
            onClick = {
                navController.navigate("main/$userName") {
                    popUpTo("onboarding") { inclusive = true } // Видаляємо онбординг зі стеку
                }
            },
            enabled = userName.isNotBlank()
        ) {
            Text(if (userName.isBlank()) "Розпочати" else "Привіт, $userName! Розпочати")
        }
    }
}