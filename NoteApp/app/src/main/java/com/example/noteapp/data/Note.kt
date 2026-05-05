package com.example.noteapp.data

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val priority: Int,
    val category: String = "Особисте",
    val isFavorite: Boolean = false,
    val estimatedTime: Int = 0,
    val sourceUrl: String = ""
)