package com.example.noteapp.ui.grid

import androidx.lifecycle.ViewModel
import com.example.noteapp.data.Tag
import com.example.noteapp.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TagsGridUiState(
    val isVertical: Boolean = true,
    val tags: List<Tag> = emptyList()
)

class TagsGridViewModel(
    repository: NotesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TagsGridUiState(
            isVertical = true,
            tags = repository.getTags()
        )
    )
    val uiState: StateFlow<TagsGridUiState> = _uiState.asStateFlow()

    fun setOrientation(vertical: Boolean) {
        _uiState.update { it.copy(isVertical = vertical) }
    }
}

