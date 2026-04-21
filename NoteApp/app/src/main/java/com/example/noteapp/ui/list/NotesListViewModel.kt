package com.example.noteapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.Note
import com.example.noteapp.repository.NotesRepository
import com.example.noteapp.settings.SettingsRepository
import com.example.noteapp.settings.SortMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesListUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val selectedTag: String? = null,
    val favoritesOnly: Boolean = false,
    val availableTags: List<String> = emptyList(),
    val sortMode: SortMode = SortMode.PRIORITY_ASC,
    val markdownEnabled: Boolean = true
) {
    val totalCount: Int get() = notes.size
}

class NotesListViewModel(
    private val repository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotesListUiState(
            isLoading = true,
            availableTags = repository.getTagNames()
        )
    )
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.ensureSeedData()
            combine(
                repository.getNotesFlow(),
                settingsRepository.settingsFlow
            ) { notes, settings ->
                notes to settings
            }.collect { (notes, settings) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = notes,
                        sortMode = settings.sortMode,
                        markdownEnabled = settings.markdownEnabled
                    )
                }
            }
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
        viewModelScope.launch {
            val state = _uiState.value
            repository.addNote(title = title, content = content, category = state.selectedTag ?: "Особисте")
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
        }
    }

    fun toggleFavorite(noteId: String) {
        viewModelScope.launch {
            val current = _uiState.value.notes.firstOrNull { it.id == noteId } ?: return@launch
            repository.toggleFavorite(noteId = noteId, isFavorite = !current.isFavorite)
        }
    }

    fun filteredNotes(): List<Note> {
        val state = _uiState.value
        val selectedTag = state.selectedTag
        return state.notes
            .filter { note ->
                (!state.favoritesOnly || note.isFavorite) &&
                    (selectedTag == null || note.category == selectedTag)
            }
            .sortedWith(
                when (state.sortMode) {
                    SortMode.PRIORITY_ASC -> compareBy { it.priority }
                    SortMode.PRIORITY_DESC -> compareByDescending { it.priority }
                    SortMode.TITLE_ASC -> compareBy { it.title.lowercase() }
                }
            )
    }
}

