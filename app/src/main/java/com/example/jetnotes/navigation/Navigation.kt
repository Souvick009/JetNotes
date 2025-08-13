package com.example.jetnotes.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.jetnotes.screens.createNotesScreen
import com.example.jetnotes.screens.homeScreen
import com.example.jetnotes.viewmodel.NotesViewModel

@Composable
fun navController() {
    val navController = rememberNavController()
    val viewModel: NotesViewModel = hiltViewModel()

    NavHost(
        navController = navController, startDestination = HomeScreenRoute,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it })
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it })
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it })
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        }
    ) {
        composable<HomeScreenRoute> {
            homeScreen(navController = navController, viewModel = viewModel)
        }

        composable<CreateNoteScreenRoute> {
            var noteDTO = it.toRoute<CreateNoteScreenRoute>()
            createNotesScreen(
                navController = navController,
                viewModel = viewModel,
                noteID = noteDTO.id,
                title = noteDTO.title,
                desc = noteDTO.desc,
                progress = noteDTO.progress,
                color = noteDTO.color
            )
        }
    }
}