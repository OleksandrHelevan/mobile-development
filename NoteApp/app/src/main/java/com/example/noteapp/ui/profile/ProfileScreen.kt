package com.example.noteapp.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteapp.settings.SortMode
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onNameChange: (String) -> Unit,
    onSortModeChange: (SortMode) -> Unit,
    onMarkdownEnabledChange: (Boolean) -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text("Налаштування", style = MaterialTheme.typography.titleLarge)
        Text("Mini Notion Notes", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Ваше ім'я") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        Text("Сортування списку", style = MaterialTheme.typography.titleMedium)
        SortMode.entries.forEach { mode ->
            androidx.compose.material3.FilterChip(
                selected = state.sortMode == mode,
                onClick = { onSortModeChange(mode) },
                label = { Text(mode.name.replace('_', ' ')) },
                modifier = Modifier.padding(top = 8.dp, end = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        Text("Markdown-підсвітка у списку", style = MaterialTheme.typography.titleMedium)
        Switch(
            checked = state.markdownEnabled,
            onCheckedChange = onMarkdownEnabledChange
        )
    }
}

@Preview(showBackground = true, name = "Profile Light")
@Composable
private fun ProfileLightPreview() {
    NoteAppTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            state = ProfileUiState(name = "Олександр"),
            onNameChange = {},
            onSortModeChange = {},
            onMarkdownEnabledChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Dark")
@Composable
private fun ProfileDarkPreview() {
    NoteAppTheme(darkTheme = true, dynamicColor = false) {
        ProfileScreen(
            state = ProfileUiState(name = "Олександр"),
            onNameChange = {},
            onSortModeChange = {},
            onMarkdownEnabledChange = {}
        )
    }
}