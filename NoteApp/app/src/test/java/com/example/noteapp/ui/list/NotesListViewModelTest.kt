package com.example.noteapp.ui.list

import com.example.noteapp.data.Note
import com.example.noteapp.repository.NotesRepository
import com.example.noteapp.settings.SettingsRepository
import com.example.noteapp.settings.UserSettings
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NotesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NotesRepository
    private lateinit var settingsRepository: SettingsRepository

    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val settingsFlow = MutableStateFlow(UserSettings())

    private lateinit var viewModel: NotesListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        settingsRepository = mock()

        whenever(repository.getNotesFlow()).thenReturn(notesFlow)
        whenever(settingsRepository.settingsFlow).thenReturn(settingsFlow)

        viewModel = NotesListViewModel(repository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filteredNotes returns notes of selected category`() = runTest {
        val notes = listOf(
            Note(id = "1", title = "Work Note", content = "C1", category = "Work", priority = 1),
            Note(id = "2", title = "Home Note", content = "C2", category = "Home", priority = 2)
        )

        notesFlow.value = notes
        advanceUntilIdle()

        viewModel.selectTag("Work")

        val result = viewModel.filteredNotes()
        assertEquals(1, result.size)
        assertEquals("Work", result[0].category)
    }

    @Test
    fun `syncWithNetwork sets isOffline state on exception`() = runTest {
        whenever(repository.syncNotesFromNetwork()).thenThrow(RuntimeException("Network error"))

        viewModel.syncWithNetwork()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOffline)
        assertEquals("Помилка мережі. Показано локальні дані.", viewModel.uiState.value.networkError)
    }

    @Test
    fun `filteredNotes returns empty list when no favorites exist`() = runTest {
        val notes = listOf(
            Note(id = "1", title = "Normal", content = "C1", category = "Work", priority = 1, isFavorite = false)
        )
        notesFlow.value = notes
        advanceUntilIdle()

        viewModel.setFavoritesOnly(true)
        val result = viewModel.filteredNotes()
        assertTrue(result.isEmpty())
    }
}