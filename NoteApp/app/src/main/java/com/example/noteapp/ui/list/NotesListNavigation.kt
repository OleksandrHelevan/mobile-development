package com.example.noteapp.ui.list

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
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "notes"
    ) {
        composable("notes") {
            NotesListScreen(
                onItemClick = { noteId -> navController.navigate("note/$noteId") }
            )
        }
        composable(
            route = "note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId").orEmpty()
            NoteDetailsScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

