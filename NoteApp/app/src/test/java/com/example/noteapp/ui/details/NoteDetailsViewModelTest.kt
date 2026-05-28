package com.example.noteapp.ui.details

import com.example.noteapp.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NotesRepository
    private lateinit var viewModel: NoteDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = NoteDetailsViewModel("new", repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `form becomes valid when inputs are correct`() = runTest {
        viewModel.updateTitle("Valid Title")
        viewModel.updateTime("100")
        viewModel.updateCategory("Work")

        viewModel.validateTitle()
        viewModel.validateTime()
        viewModel.validateCategory()

        assertTrue(viewModel.formState.value.isValid)
        assertNull(viewModel.formState.value.titleError)
    }

    @Test
    fun `form becomes invalid with short title and incorrect time`() = runTest {
        viewModel.updateTitle("Hi")
        viewModel.updateTime("9999")

        viewModel.validateTitle()
        viewModel.validateTime()

        assertFalse(viewModel.formState.value.isValid)
        assertEquals("Мінімум 3 символи", viewModel.formState.value.titleError)
        assertEquals("Число від 1 до 1000", viewModel.formState.value.timeError)
    }

    @Test
    fun `saveNote calls repository addNote when form is valid`() = runTest {
        viewModel.updateTitle("Valid Title")
        viewModel.updateTime("50")
        viewModel.updateCategory("General")

        var isSavedCalled = false

        viewModel.saveNote { isSavedCalled = true }
        advanceUntilIdle()

        verify(repository).addNote(
            title = "Valid Title",
            content = "",
            category = "General",
            priority = 5,
            isFavorite = false,
            estimatedTime = 50,
            sourceUrl = "",
            imagePath = null,
            latitude = null,
            longitude = null
        )
        assertTrue(isSavedCalled)
    }

    @Test
    fun `saveNote does nothing when form is invalid`() = runTest {
        viewModel.updateTitle("Hi")

        var isSavedCalled = false
        viewModel.saveNote { isSavedCalled = true }

        advanceUntilIdle()

        assertFalse(isSavedCalled)
    }
}