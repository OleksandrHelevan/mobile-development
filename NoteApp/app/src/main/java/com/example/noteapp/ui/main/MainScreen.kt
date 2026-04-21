package com.example.noteapp.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.di.ServiceLocator
import com.example.noteapp.ui.list.NotesListNavHost
import com.example.noteapp.ui.grid.TagsGridScreen
import com.example.noteapp.ui.profile.ProfileScreen
import com.example.noteapp.ui.profile.ProfileViewModel

@Composable
fun MainScreen(userName: String) {
    val context = LocalContext.current
    val tabNavController = rememberNavController()
    val profileVm: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(ServiceLocator.settingsRepository(context))
    )
    val profileState by profileVm.uiState.collectAsStateWithLifecycle()

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
            composable("list") { NotesListNavHost() }
            composable("grid") { TagsGridScreen() }
            composable("profile") {
                ProfileScreen(
                    state = profileState,
                    onNameChange = profileVm::setName,
                    onSortModeChange = profileVm::setSortMode,
                    onMarkdownEnabledChange = profileVm::setMarkdownEnabled
                )
            }
        }
    }
}