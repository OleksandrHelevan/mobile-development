package com.example.noteapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val priority: Int,
    val category: String,
    val isFavorite: Boolean,
    val estimatedTime: Int,
    val sourceUrl: String = "",
    val imagePath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)