// ui/main/MainScreen.kt
package com.example.noteapp.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.ui.list.NotesListScreen
import com.example.noteapp.ui.grid.TagsGridScreen

@Composable
fun MainScreen(userName: String) {
    val tabNavController = rememberNavController()
    var currentUserName by remember { mutableStateOf(userName) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "list",
                    onClick = { tabNavController.navigate("list") },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Список") }
                )
                NavigationBarItem(
                    selected = currentRoute == "grid",
                    onClick = { tabNavController.navigate("grid") },
                    icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                    label = { Text("Плитка") }
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = { tabNavController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профіль") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = "list",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("list") { NotesListScreen() }
            composable("grid") { TagsGridScreen() }
            composable("profile") {
                ProfileScreen(currentUserName) { currentUserName = it }
            }
        }
    }
}

@Composable
fun ProfileScreen(name: String, onNameChange: (String) -> Unit) {
    androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
        Text("Інформація про додаток", style = MaterialTheme.typography.titleLarge)
        Text("Назва: NoteApp")
        Text("Версія: 1.0.5")
        Text("Розробник: Олександр")

        androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Ваше ім'я") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}