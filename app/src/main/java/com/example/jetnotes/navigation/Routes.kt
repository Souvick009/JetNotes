package com.example.jetnotes.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeScreenRoute

@Serializable
data class CreateNoteScreenRoute(
    val id: Int? = null,
    val title: String,
    val desc: String,
    val progress: Int = 0,
    val color: String? = null
)