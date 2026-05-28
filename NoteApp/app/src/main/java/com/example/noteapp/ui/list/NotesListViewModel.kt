package com.example.noteapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.Note
import com.example.noteapp.repository.NotesRepository
import com.example.noteapp.settings.SettingsRepository
import com.example.noteapp.settings.SortMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NotesListUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val networkError: String? = null,
    val isActionLoading: Boolean = false,
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
        observeLocalData()
        syncWithNetwork()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
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

    fun syncWithNetwork() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isOffline = false, networkError = null) }
                repository.syncNotesFromNetwork()
            } catch (e: Exception) {
                android.util.Log.e("NoteAppNetwork", "Помилка під час syncWithNetwork", e)
                _uiState.update { it.copy(isOffline = true, networkError = "Помилка мережі. Показано локальні дані.") }
            }
        }
    }

    fun refreshNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            delay(1500)
            try {
                repository.syncNotesFromNetwork()
                _uiState.update { it.copy(isOffline = false, networkError = "Дані оновлено") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isOffline = true, networkError = "Не вдалося оновити") }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(networkError = null) }
    }

    fun selectTag(tag: String?) {
        _uiState.update { it.copy(selectedTag = tag) }
    }

    fun setFavoritesOnly(enabled: Boolean) {
        _uiState.update { it.copy(favoritesOnly = enabled) }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            try {
                repository.deleteNote(noteId)
                _uiState.update { it.copy(isActionLoading = false, networkError = "Нотатку видалено") }
            } catch (e: Exception) {
                android.util.Log.e("NoteAppNetwork", "Помилка під час deleteNote", e)
                _uiState.update { it.copy(isActionLoading = false, networkError = "Помилка видалення.") }
            }
        }
    }

    fun toggleFavorite(noteId: String) {
        viewModelScope.launch {
            val current = _uiState.value.notes.firstOrNull { it.id == noteId } ?: return@launch
            try {
                repository.toggleFavorite(noteId = noteId, isFavorite = !current.isFavorite)
            } catch (e: Exception) {
                android.util.Log.e("NoteAppNetwork", "Помилка під час toggleFavorite", e)
                _uiState.update { it.copy(networkError = "Помилка оновлення.") }
            }
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