package com.example.noteapp.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApi {
    @GET("notes")
    suspend fun getAllNotes(): List<NoteDto>

    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") id: String): NoteDto

    @POST("notes")
    suspend fun createNote(@Body note: NoteDto): NoteDto

    @PUT("notes/{id}")
    suspend fun updateNote(@Path("id") id: String, @Body note: NoteDto): NoteDto

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): Response<Unit>
}