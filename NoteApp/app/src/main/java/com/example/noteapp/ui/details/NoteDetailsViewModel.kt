package com.example.noteapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.Note
import com.example.noteapp.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface NoteDetailsUiState {
    data object Loading : NoteDetailsUiState
    data class Error(val message: String) : NoteDetailsUiState
    data class Success(
        val note: Note,
        val category: String?,
        val relatedNotes: List<Note>
    ) : NoteDetailsUiState
}

class NoteDetailsViewModel(
    private val noteId: String,
    private val repository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteDetailsUiState>(NoteDetailsUiState.Loading)
    val uiState: StateFlow<NoteDetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { NoteDetailsUiState.Loading }
            kotlinx.coroutines.flow.combine(
                repository.getNoteByIdFlow(noteId),
                repository.getNotesFlow()
            ) { note, allNotes ->
                note to allNotes
            }.collect { (note, allNotes) ->
                if (note == null) {
                    _uiState.update { NoteDetailsUiState.Error("Нотатку не знайдено") }
                    return@collect
                }
                val related = allNotes
                    .asSequence()
                    .filter { it.id != note.id }
                    .filter { it.category == note.category }
                    .take(5)
                    .toList()
                _uiState.update {
                    NoteDetailsUiState.Success(
                        note = note,
                        category = note.category,
                        relatedNotes = related
                    )
                }
            }
        }
    }

    class Factory(
        private val noteId: String,
        private val repository: NotesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteDetailsViewModel::class.java)) {
                return NoteDetailsViewModel(noteId = noteId, repository = repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

