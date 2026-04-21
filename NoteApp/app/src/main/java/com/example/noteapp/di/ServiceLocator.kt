package com.example.noteapp.di

import android.annotation.SuppressLint
import android.content.Context
import com.example.noteapp.data.local.NoteDatabase
import com.example.noteapp.repository.NotesRepository
import com.example.noteapp.settings.SettingsRepository

object ServiceLocator {
    @Volatile
    private var notesRepository: NotesRepository? = null

    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var settingsRepository: SettingsRepository? = null

    fun notesRepository(context: Context): NotesRepository {
        return notesRepository ?: synchronized(this) {
            notesRepository ?: NotesRepository(NoteDatabase.getInstance(context).noteDao()).also {
                notesRepository = it
            }
        }
    }

    fun settingsRepository(context: Context): SettingsRepository {
        return settingsRepository ?: synchronized(this) {
            settingsRepository ?: SettingsRepository(context.applicationContext).also {
                settingsRepository = it
            }
        }
    }
}

