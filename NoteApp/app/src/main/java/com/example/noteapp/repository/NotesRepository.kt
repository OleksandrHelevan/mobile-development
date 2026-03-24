package com.example.noteapp.repository

import com.example.noteapp.model.Note
import com.example.noteapp.model.Tag

object NotesRepository {

    val notes = listOf(
        Note("1", "Купити продукти", "Сік, курка, рис, хліб, масло", 5),
        Note("2", "Зробити лабу", "Jetpack Compose лабораторна", 2),
        Note("3", "Піти в зал", "Тренування о 18:00", 4),
        Note("4", "Інвестігейт", "SPIKE: How to embed custom html file in app ", 1),
        Note("5", "Подзвонити другу", "Обговорити проект", 3)
    )

    val tags = setOf(
        Tag("1", "Навчання"),
        Tag("2", "Робота"),
        Tag("3", "Особисте"),
        Tag("4", "Спорт")
    )

    val noteCategory = mapOf(
        "Купити продукти" to "Особисте",
        "Зробити лабу" to "Навчання",
        "Піти в зал" to "Спорт",
        "Інвестігейт" to "Робота",
        "Подзвонити другу" to "Особисте"
    )
    val sortedNotes = notes.sortedBy { it.priority }
    val tagNames = tags.map { it.name }
}