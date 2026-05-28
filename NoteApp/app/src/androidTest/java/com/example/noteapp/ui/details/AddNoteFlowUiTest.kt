package com.example.noteapp.ui.details

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.noteapp.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddNoteFlowUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addNoteFlow_navigatesAndSavesSuccessfully() {
        composeTestRule.waitForIdle()

        val isFirstRun = composeTestRule.onAllNodesWithText("Почати").fetchSemanticsNodes().isNotEmpty()

        if (isFirstRun) {
            composeTestRule.onNodeWithText("Почати").performClick()
            composeTestRule.onNodeWithText("Введіть ваше ім'я").performTextInput("Тестувальник")
            composeTestRule.onNodeWithText("Зберегти").performClick()
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("AddNoteButton").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("AddNoteButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("TitleField").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("TitleField").performTextInput("UI Тест Нотатки")
        composeTestRule.onNodeWithTag("ContentField").performTextInput("Тестовий опис")

        composeTestRule.onNodeWithText("Додаткові параметри").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Очікуваний час (хв)")
            .performScrollTo()
            .performTextInput("10")

        composeTestRule.onNodeWithText("Категорія")
            .performScrollTo()
            .performClick()

        composeTestRule.onAllNodesWithText("Особисте")
            .onLast()
            .performClick()

        composeTestRule.onNodeWithTag("SaveButton")
            .performClick()


        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("UI Тест Нотатки").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("UI Тест Нотатки")
            .onFirst()
            .assertIsDisplayed()
    }
}