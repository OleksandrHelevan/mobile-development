package com.example.noteapp.data.network

import com.example.noteapp.data.local.NoteEntity
import com.google.gson.annotations.SerializedName

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val priority: Int,
    val category: String,
    @SerializedName("isFavorite") val isFavorite: Boolean = false,
    val estimatedTime: Int,
    val sourceUrl: String? = null,
    val imagePath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun toEntity(): NoteEntity {
        return NoteEntity(
            id = this.id,
            title = this.title,
            content = this.content,
            priority = this.priority,
            category = this.category,
            isFavorite = this.isFavorite,
            estimatedTime = this.estimatedTime,
            sourceUrl = this.sourceUrl ?: "",
            imagePath = this.imagePath,
            latitude = this.latitude,
            longitude = this.longitude
        )
    }
}