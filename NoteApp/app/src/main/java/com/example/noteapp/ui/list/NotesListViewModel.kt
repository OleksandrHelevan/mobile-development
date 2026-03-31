package com.example.noteapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.model.Note
import com.example.noteapp.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class NotesListUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val selectedTag: String? = null,
    val favoritesOnly: Boolean = false,
    val availableTags: List<String> = emptyList()
) {
    val totalCount: Int get() = notes.size
}

class NotesListViewModel(
    private val repository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotesListUiState(
            isLoading = true,
            availableTags = repository.getTagNames()
        )
    )
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800L)
            _uiState.update { it.copy(isLoading = false, notes = repository.getNotes()) }
        }
    }

    fun selectTag(tag: String?) {
        _uiState.update { it.copy(selectedTag = tag) }
    }

    fun setFavoritesOnly(enabled: Boolean) {
        _uiState.update { it.copy(favoritesOnly = enabled) }
    }

    fun addNote(title: String, content: String) {
        if (title.isBlank()) return
        val newNote = Note(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            priority = 1,
            isFavorite = false
        )
        _uiState.update { it.copy(notes = it.notes + newNote) }
    }

    fun deleteNote(noteId: String) {
        _uiState.update { it.copy(notes = it.notes.filterNot { n -> n.id == noteId }) }
    }

    fun toggleFavorite(noteId: String) {
        _uiState.update { state ->
            state.copy(
                notes = state.notes.map { n ->
                    if (n.id == noteId) n.copy(isFavorite = !n.isFavorite) else n
                }
            )
        }
    }

    fun filteredNotes(): List<Note> {
        val state = _uiState.value
        val selectedTag = state.selectedTag
        return state.notes
            .filter { note ->
                (!state.favoritesOnly || note.isFavorite) &&
                    (selectedTag == null || repository.getCategoryForNoteTitle(note.title) == selectedTag)
            }
            .sortedBy { it.priority }
    }
}

