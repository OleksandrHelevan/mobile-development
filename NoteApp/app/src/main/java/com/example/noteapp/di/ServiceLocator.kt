package com.example.noteapp.di

import android.annotation.SuppressLint
import android.content.Context
import com.example.noteapp.data.local.NoteDatabase
import com.example.noteapp.data.network.NoteApi
import com.example.noteapp.repository.NotesRepository
import com.example.noteapp.settings.SettingsRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    @Volatile
    private var notesRepository: NotesRepository? = null

    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var settingsRepository: SettingsRepository? = null

    @Volatile
    private var noteApi: NoteApi? = null
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

    private fun getApi(): NoteApi {
        return noteApi ?: synchronized(this) {
            noteApi ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NoteApi::class.java).also {
                    noteApi = it
                }
        }
    }

    fun notesRepository(context: Context): NotesRepository {
        return notesRepository ?: synchronized(this) {
            notesRepository ?: NotesRepository(
                noteDao = NoteDatabase.getInstance(context).noteDao(),
                api = getApi()
            ).also {
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