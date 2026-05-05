package com.example.noteapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
fun MainScreen(
    userName: String,
    widthSizeClass: WindowWidthSizeClass
) {
    val context = LocalContext.current
    val tabNavController = rememberNavController()
    val profileVm: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(ServiceLocator.settingsRepository(context))
    )
    val profileState by profileVm.uiState.collectAsStateWithLifecycle()

    val useNavRail = widthSizeClass == WindowWidthSizeClass.Expanded

    Row(modifier = Modifier.fillMaxSize()) {
        if (useNavRail) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets = WindowInsets(0, 0, 0, 0),
                // ВИДАЛЕНО: блок header з іконкою EditNote
                header = null
            ) {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Додамо невеликий відступ зверху, щоб перша кнопка не "прилипала" до краю
                Spacer(Modifier.height(8.dp))

                NavigationRailItem(
                    selected = currentRoute == "list",
                    onClick = { tabNavController.navigate("list") },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    label = { Text("Список") }
                )
                NavigationRailItem(
                    selected = currentRoute == "grid",
                    onClick = { tabNavController.navigate("grid") },
                    icon = { Icon(Icons.Default.GridView, null) },
                    label = { Text("Плитка") }
                )
                NavigationRailItem(
                    selected = currentRoute == "profile",
                    onClick = { tabNavController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Профіль") }
                )
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar {
                        val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        NavigationBarItem(
                            selected = currentRoute == "list",
                            onClick = { tabNavController.navigate("list") },
                            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                            label = { Text("Список") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "grid",
                            onClick = { tabNavController.navigate("grid") },
                            icon = { Icon(Icons.Default.GridView, null) },
                            label = { Text("Плитка") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "profile",
                            onClick = { tabNavController.navigate("profile") },
                            icon = { Icon(Icons.Default.Person, null) },
                            label = { Text("Профіль") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                NavHost(
                    navController = tabNavController,
                    startDestination = "list",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("list") {
                        NotesListNavHost(widthSizeClass = widthSizeClass)
                    }
                    composable("grid") {
                        TagsGridScreen(widthSizeClass = widthSizeClass)
                    }
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
    }
}