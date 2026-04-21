package com.example.noteapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
public data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val priority: Int,
    val category: String,
    val isFavorite: Boolean
)

