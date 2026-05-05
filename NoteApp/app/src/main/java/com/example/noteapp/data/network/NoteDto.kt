package com.example.noteapp.data.network

import com.example.noteapp.data.local.NoteEntity
import com.google.gson.annotations.SerializedName

data class NoteDto(
    @SerializedName(value = "id", alternate = ["_id"])
    val id: String?,
    val title: String,
    val content: String,
    val priority: Int,
    val category: String?,
    val isFavorite: Boolean?,
    val estimatedTime: Int?,
    val sourceUrl: String?
) {
    fun toEntity(): NoteEntity = NoteEntity(
        id = id ?: System.currentTimeMillis().toString(),
        title = title,
        content = content,
        priority = priority,
        category = category ?: "Особисте",
        isFavorite = isFavorite ?: false,
        estimatedTime = estimatedTime ?: 0,
        sourceUrl = sourceUrl ?: ""
    )
}