package com.example.jetnotes.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeScreenRoute

@Serializable
data class CreateNoteScreenRoute(
    val id: Int? = null,
    val title: String? = null,
    val desc: String? = null
)