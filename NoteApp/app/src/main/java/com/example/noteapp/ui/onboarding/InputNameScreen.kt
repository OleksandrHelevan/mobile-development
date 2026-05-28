package com.example.noteapp.ui.onboarding

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.noteapp.di.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InputNameScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsRepository = remember { ServiceLocator.settingsRepository(context) }
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Введіть ваше ім'я") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    settingsRepository.setUserName(name)
                    withContext(Dispatchers.Main) {
                        navController.navigate("main/${Uri.encode(name)}") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Зберегти")
        }
    }
}