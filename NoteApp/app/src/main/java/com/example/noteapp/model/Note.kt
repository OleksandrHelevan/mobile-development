package com.example.noteapp.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val priority: Int,
    val isFavorite: Boolean = false
)