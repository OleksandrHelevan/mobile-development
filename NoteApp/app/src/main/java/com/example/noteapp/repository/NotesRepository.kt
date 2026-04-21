package com.example.noteapp.repository

import com.example.noteapp.data.local.NoteDao
import com.example.noteapp.data.local.NoteEntity
import com.example.noteapp.data.Note
import com.example.noteapp.data.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepository(
    private val noteDao: NoteDao
) {

    private val seedNotes = listOf(
        Note("1", "Купити продукти", "- [ ] Сік\n- [ ] Курка\n- [ ] Рис\n- [ ] Хліб", 5, "Особисте"),
        Note("2", "Зробити лабу", "## ЛР №8\nПеренести дані в Room + DataStore", 2, "Навчання"),
        Note("3", "Піти в зал", "**Тренування** о 18:00\nФокус: спина + кардіо", 4, "Спорт"),
        Note("4", "Інвестігейт", "SPIKE: How to embed custom html file in app", 1, "Робота"),
        Note("5", "Подзвонити другу", "Обговорити ідею міні-Notion", 3, "Особисте")
    )

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

    suspend fun addNote(title: String, content: String, category: String = "Особисте") {
        val note = Note(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            priority = 1,
            category = category,
            isFavorite = false
        )
        noteDao.insert(note.toEntity())
    }

    suspend fun deleteNote(noteId: String) {
        noteDao.deleteById(noteId)
    }

    suspend fun toggleFavorite(noteId: String, isFavorite: Boolean) {
        noteDao.updateFavorite(id = noteId, isFavorite = isFavorite)
    }

    suspend fun ensureSeedData() {
        if (noteDao.countNotes() == 0) {
            noteDao.insertAll(seedNotes.map { it.toEntity() })
        }
    }

    private fun NoteEntity.toDomain(): Note = Note(
        id = id,
        title = title,
        content = content,
        priority = priority,
        category = category,
        isFavorite = isFavorite
    )

    private fun Note.toEntity(): NoteEntity = NoteEntity(
        id = id,
        title = title,
        content = content,
        priority = priority,
        category = category,
        isFavorite = isFavorite
    )
}