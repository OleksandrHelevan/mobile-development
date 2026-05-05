package com.example.noteapp.ui.list

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.noteapp.ui.details.NoteDetailsScreen

@Composable
fun NotesListNavHost(
    widthSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "notes"
    ) {
        composable("notes") {
            NotesListScreen(
                widthSizeClass = widthSizeClass,
                onAddOrEditClick = { noteId -> navController.navigate("note/$noteId") }
            )
        }
        composable(
            route = "note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId").orEmpty()
            NoteDetailsScreen(
                noteId = noteId,
                widthSizeClass = widthSizeClass,
                onBack = { navController.popBackStack() }
            )
        }
    }
}