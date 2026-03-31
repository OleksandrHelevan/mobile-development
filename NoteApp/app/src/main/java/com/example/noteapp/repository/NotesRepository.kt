package com.example.noteapp.repository

import com.example.noteapp.model.Note
import com.example.noteapp.model.Tag

class NotesRepository {

    private val seedNotes = listOf(
        Note("1", "Купити продукти", "Сік, курка, рис, хліб, масло", 5),
        Note("2", "Зробити лабу", "Jetpack Compose лабораторна", 2),
        Note("3", "Піти в зал", "Тренування о 18:00", 4),
        Note("4", "Інвестігейт", "SPIKE: How to embed custom html file in app ", 1),
        Note("5", "Подзвонити другу", "Обговорити проект", 3)
    )

    private val seedTags = setOf(
        Tag("1", "Навчання"),
        Tag("2", "Робота"),
        Tag("3", "Особисте"),
        Tag("4", "Спорт")
    )

    private val noteCategoryByTitle = mapOf(
        "Купити продукти" to "Особисте",
        "Зробити лабу" to "Навчання",
        "Піти в зал" to "Спорт",
        "Інвестігейт" to "Робота",
        "Подзвонити другу" to "Особисте"
    )

    fun getNotes(): List<Note> = seedNotes

    fun getSortedNotesByPriorityAsc(): List<Note> = seedNotes.sortedBy { it.priority }

    fun findNoteById(id: String): Note? = seedNotes.firstOrNull { it.id == id }

    fun getTags(): List<Tag> = seedTags.toList()

    fun getTagNames(): List<String> = seedTags.map { it.name }

    fun getCategoryForNoteTitle(title: String): String? = noteCategoryByTitle[title]
}

object RepositoryProvider {
    val notesRepository: NotesRepository = NotesRepository()
}