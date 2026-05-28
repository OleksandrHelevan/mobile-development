package com.example.noteapp.ui.details

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.noteapp.repository.NotesRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

data class FormState(
    val title: String = "", val titleError: String? = null,
    val content: String = "", val contentError: String? = null,
    val estimatedTime: String = "", val timeError: String? = null,
    val priority: Float = 5f,
    val category: String = "", val categoryError: String? = null,
    val isFavorite: Boolean = false,
    val sourceUrl: String = "", val urlError: String? = null,
    val imagePath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAccuracy: Float? = null,
    val distanceToKyiv: Float? = null,

    val isValid: Boolean = false
)

class NoteDetailsViewModel(
    private val noteId: String?,
    private val repository: NotesRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(FormState())
    val formState = _formState.asStateFlow()

    private val KYIV_LAT = 50.4501
    private val KYIV_LNG = 30.5234

    init {
        if (noteId != null && noteId != "new") {
            loadExistingNote()
        }
    }

    private fun loadExistingNote() {
        viewModelScope.launch {
            repository.getNoteByIdFlow(noteId!!).collect { note ->
                if (note != null) {
                    _formState.update {
                        it.copy(
                            title = note.title,
                            content = note.content,
                            estimatedTime = note.estimatedTime.toString(),
                            priority = note.priority.toFloat(),
                            category = note.category,
                            isFavorite = note.isFavorite,
                            sourceUrl = note.sourceUrl,
                            imagePath = note.imagePath,
                            latitude = note.latitude,
                            longitude = note.longitude
                        )
                    }
                    if (note.latitude != null && note.longitude != null) {
                        calculateDistance(note.latitude, note.longitude)
                    }
                    checkFormValidity()
                }
            }
        }
    }

    fun updateTitle(newValue: String) {
        _formState.update { it.copy(title = newValue) }
        checkFormValidity()
    }

    fun updateContent(newValue: String) {
        _formState.update { it.copy(content = newValue) }
        checkFormValidity()
    }

    fun updateTime(newValue: String) {
        _formState.update { it.copy(estimatedTime = newValue) }
        checkFormValidity()
    }

    fun updatePriority(newValue: Float) {
        _formState.update { it.copy(priority = newValue) }
    }

    fun updateCategory(newValue: String) {
        _formState.update {
            it.copy(
                category = newValue,
                categoryError = null
            )
        }
        checkFormValidity()
    }

    fun toggleFavorite(newValue: Boolean) {
        _formState.update { it.copy(isFavorite = newValue) }
    }

    fun updateUrl(newValue: String) {
        _formState.update { it.copy(sourceUrl = newValue) }
        checkFormValidity()
    }

    fun validateTitle() {
        val error = if (_formState.value.title.trim().length < 3) "Мінімум 3 символи" else null
        _formState.update { it.copy(titleError = error) }
        checkFormValidity()
    }

    fun validateTime() {
        val timeInt = _formState.value.estimatedTime.toIntOrNull()
        val error = if (timeInt == null || timeInt !in 1..1000) "Число від 1 до 1000" else null
        _formState.update { it.copy(timeError = error) }
        checkFormValidity()
    }

    fun validateCategory() {
        val error = if (_formState.value.category.isBlank()) "Оберіть категорію" else null
        _formState.update { it.copy(categoryError = error) }
        checkFormValidity()
    }

    fun validateUrl() {
        val url = _formState.value.sourceUrl
        val error = if (url.isNotBlank() && !Patterns.WEB_URL.matcher(url)
                .matches()
        ) "Некоректний URL" else null
        _formState.update { it.copy(urlError = error) }
        checkFormValidity()
    }

    private fun checkFormValidity() {
        val state = _formState.value
        val isValid = state.title.length >= 3 &&
                (state.estimatedTime.toIntOrNull() ?: 0) in 1..1000 &&
                state.category.isNotBlank() &&
                (state.sourceUrl.isBlank() || Patterns.WEB_URL.matcher(state.sourceUrl).matches())

        _formState.update { it.copy(isValid = isValid) }
    }


    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            val filename = "note_img_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            _formState.update { it.copy(imagePath = file.absolutePath) }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {
        viewModelScope.launch {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()

                if (location != null) {
                    _formState.update {
                        it.copy(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            locationAccuracy = location.accuracy
                        )
                    }
                    calculateDistance(location.latitude, location.longitude)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateDistance(lat: Double, lng: Double) {
        val results = FloatArray(1)
        Location.distanceBetween(lat, lng, KYIV_LAT, KYIV_LNG, results)
        _formState.update { it.copy(distanceToKyiv = results[0]) }
    }

    fun saveNote(onSaved: () -> Unit) {
        if (!_formState.value.isValid) return

        viewModelScope.launch {
            val state = _formState.value
            val time = state.estimatedTime.toIntOrNull() ?: 0

            if (noteId == null || noteId == "new") {
                repository.addNote(
                    title = state.title,
                    content = state.content,
                    category = state.category,
                    priority = state.priority.toInt(),
                    isFavorite = state.isFavorite,
                    estimatedTime = time,
                    sourceUrl = state.sourceUrl,
                    imagePath = state.imagePath,
                    latitude = state.latitude,
                    longitude = state.longitude
                )
            } else {
                repository.updateNote(
                    id = noteId,
                    title = state.title,
                    content = state.content,
                    category = state.category,
                    priority = state.priority.toInt(),
                    isFavorite = state.isFavorite,
                    estimatedTime = time,
                    sourceUrl = state.sourceUrl,
                    imagePath = state.imagePath,
                    latitude = state.latitude,
                    longitude = state.longitude
                )
            }
            onSaved()
        }
    }

    class Factory(
        private val noteId: String?,
        private val repository: NotesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NoteDetailsViewModel(noteId, repository) as T
        }
    }
}