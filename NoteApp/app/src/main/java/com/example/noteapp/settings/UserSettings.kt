package com.example.noteapp.settings

enum class SortMode {
    PRIORITY_ASC,
    PRIORITY_DESC,
    TITLE_ASC
}

data class UserSettings(
    val userName: String = "",
    val sortMode: SortMode = SortMode.PRIORITY_ASC,
    val markdownEnabled: Boolean = true
)

