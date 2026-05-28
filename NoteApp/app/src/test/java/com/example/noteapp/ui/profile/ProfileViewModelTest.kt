package com.example.noteapp.ui.profile

import com.example.noteapp.settings.SettingsRepository
import com.example.noteapp.settings.SortMode
import com.example.noteapp.settings.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: ProfileViewModel

    private val settingsFlow = MutableStateFlow(UserSettings())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        settingsRepository = mock()

        whenever(settingsRepository.settingsFlow).thenReturn(settingsFlow)

        viewModel = ProfileViewModel(settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setName calls repository setUserName`() = runTest {
        viewModel.setName("NewName")
        advanceUntilIdle()

        verify(settingsRepository).setUserName("NewName")
    }

    @Test
    fun `setSortMode calls repository setSortMode`() = runTest {
        viewModel.setSortMode(SortMode.PRIORITY_DESC)
        advanceUntilIdle()

        verify(settingsRepository).setSortMode(SortMode.PRIORITY_DESC)
    }

    @Test
    fun `setMarkdownEnabled calls repository setMarkdownEnabled`() = runTest {
        viewModel.setMarkdownEnabled(false)
        advanceUntilIdle()

        verify(settingsRepository).setMarkdownEnabled(false)
    }
}