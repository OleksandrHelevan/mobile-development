package com.example.noteapp.repository

import com.example.noteapp.data.local.NoteDao
import com.example.noteapp.data.local.NoteEntity
import com.example.noteapp.data.Note
import com.example.noteapp.data.Tag
import com.example.noteapp.data.network.NoteApi
import com.example.noteapp.data.network.NoteDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.File

class NotesRepository(
    private val noteDao: NoteDao,
    private val api: NoteApi
) {
    private val seedTags = setOf(
        Tag("1", "Навчання"),
        Tag("2", "Робота"),
        Tag("3", "Особисте"),
        Tag("4", "Спорт")
    )

    fun getNotesFlow(): Flow<List<Note>> = noteDao.getAllNotesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getNoteByIdFlow(id: String): Flow<Note?> = noteDao.getNoteByIdFlow(id).map { it?.toDomain() }

    fun getTags(): List<Tag> = seedTags.toList()

    fun getTagNames(): List<String> = seedTags.map { it.name }

    suspend fun syncNotesFromNetwork() {
        val networkNotes = api.getAllNotes()
        noteDao.insertAll(networkNotes.map { it.toEntity() })
    }

    suspend fun fetchSingleNoteFromNetwork(id: String) {
        val networkNote = api.getNoteById(id)
        noteDao.insert(networkNote.toEntity())
    }

    suspend fun addNote(
        title: String,
        content: String,
        category: String = "Особисте",
        priority: Int = 5,
        isFavorite: Boolean = false,
        estimatedTime: Int = 0,
        sourceUrl: String = "",
        imagePath: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        val newNote = NoteDto(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            priority = priority,
            category = category,
            isFavorite = isFavorite,
            estimatedTime = estimatedTime,
            sourceUrl = sourceUrl,
            // ПЕРЕДАЄМО ДАНІ НА СЕРВЕР
            imagePath = imagePath,
            latitude = latitude,
            longitude = longitude
        )
        val createdNote = api.createNote(newNote)
        noteDao.insert(createdNote.toEntity())
    }

    suspend fun deleteNote(noteId: String) {
        val localNote = noteDao.getNoteByIdFlow(noteId).firstOrNull()

        val response = api.deleteNote(noteId)
        if (response.isSuccessful || response.code() == 404) {
            noteDao.deleteById(noteId)

            // Видаляємо фізичний файл зображення
            localNote?.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
        } else {
            throw Exception("Помилка сервера: ${response.code()}")
        }
    }

    suspend fun toggleFavorite(noteId: String, isFavorite: Boolean) {
        val currentNoteEntity = noteDao.getNoteByIdFlow(noteId).firstOrNull()
            ?: throw Exception("Нотатку не знайдено локально")

        val updatedNoteDto = NoteDto(
            id = currentNoteEntity.id,
            title = currentNoteEntity.title,
            content = currentNoteEntity.content,
            priority = currentNoteEntity.priority,
            category = currentNoteEntity.category,
            isFavorite = isFavorite,
            estimatedTime = currentNoteEntity.estimatedTime,
            sourceUrl = currentNoteEntity.sourceUrl,
            // Не забуваємо відправити існуюче фото та локацію, щоб вони не затерлися!
            imagePath = currentNoteEntity.imagePath,
            latitude = currentNoteEntity.latitude,
            longitude = currentNoteEntity.longitude
        )

        val resultFromServer = api.updateNote(id = noteId, note = updatedNoteDto)
        noteDao.insert(resultFromServer.toEntity())
    }

    private fun NoteEntity.toDomain(): Note = Note(
        id = id,
        title = title,
        content = content,
        priority = priority,
        category = category,
        isFavorite = isFavorite,
        estimatedTime = estimatedTime,
        sourceUrl = sourceUrl,
        imagePath = imagePath,
        latitude = latitude,
        longitude = longitude
    )

    suspend fun updateNote(
        id: String,
        title: String,
        content: String,
        category: String,
        priority: Int,
        isFavorite: Boolean,
        estimatedTime: Int,
        sourceUrl: String,
        imagePath: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        val updatedDto = NoteDto(
            id = id,
            title = title,
            content = content,
            priority = priority,
            category = category,
            isFavorite = isFavorite,
            estimatedTime = estimatedTime,
            sourceUrl = sourceUrl,
            imagePath = imagePath,
            latitude = latitude,
            longitude = longitude
        )
        val resultFromServer = api.updateNote(id = id, note = updatedDto)
        noteDao.insert(resultFromServer.toEntity())
    }
}