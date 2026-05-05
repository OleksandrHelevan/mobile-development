package com.example.noteapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteapp.settings.SortMode
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onNameChange: (String) -> Unit,
    onSortModeChange: (SortMode) -> Unit,
    onMarkdownEnabledChange: (Boolean) -> Unit
) {
    val isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = if (isExpanded) 600.dp else Double.MAX_VALUE.dp)
                .fillMaxWidth()
        ) {
            Text("Налаштування", style = MaterialTheme.typography.titleLarge)
            Text("NoteApp", color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Користувач", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        label = { Text("Ваше ім'я") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Інтерфейс та сортування", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Text("Сортування списку", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.sortMode == mode,
                        onClick = { onSortModeChange(mode) },
                        label = { Text(mode.name.replace('_', ' ')) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Markdown-підсвітка", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "Відображати стилі тексту у загальному списку",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.markdownEnabled,
                    onCheckedChange = onMarkdownEnabledChange
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Profile Tablet", widthDp = 900, heightDp = 600)
@Composable
private fun ProfileTabletPreview() {
    NoteAppTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            state = ProfileUiState(name = "Oleksandr", sortMode = SortMode.PRIORITY_ASC),
            widthSizeClass = WindowWidthSizeClass.Expanded,
            onNameChange = {},
            onSortModeChange = {},
            onMarkdownEnabledChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Phone")
@Composable
private fun ProfilePhonePreview() {
    NoteAppTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(
            state = ProfileUiState(name = "Олександр"),
            widthSizeClass = WindowWidthSizeClass.Compact,
            onNameChange = {},
            onSortModeChange = {},
            onMarkdownEnabledChange = {}
        )
    }
}