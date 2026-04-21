package com.example.noteapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.noteapp.settings.SettingsRepository
import com.example.noteapp.settings.SortMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val sortMode: SortMode = SortMode.PRIORITY_ASC,
    val markdownEnabled: Boolean = true
)

class ProfileViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = settingsRepository.settingsFlow
        .map { settings ->
            ProfileUiState(
                name = settings.userName,
                sortMode = settings.sortMode,
                markdownEnabled = settings.markdownEnabled
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )

    fun setName(name: String) {
        viewModelScope.launch {
            settingsRepository.setUserName(name)
        }
    }

    fun setSortMode(mode: SortMode) {
        viewModelScope.launch {
            settingsRepository.setSortMode(mode)
        }
    }

    fun setMarkdownEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setMarkdownEnabled(enabled)
        }
    }

    class Factory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(settingsRepository = settingsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

