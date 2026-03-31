package com.example.noteapp.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.repository.RepositoryProvider
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
@Preview(showBackground = false)
fun NotesListScreen(
    onItemClick: (String) -> Unit = {}
) {
    val vm: NotesListViewModel = viewModel(
        factory = NotesListViewModelFactory(RepositoryProvider.notesRepository)
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("+ Додати нотатку")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            state.availableTags.forEach { tagName ->
                FilterChip(
                    selected = state.selectedTag == tagName,
                    onClick = {
                        vm.selectTag(if (state.selectedTag == tagName) null else tagName)
                    },
                    label = { Text(tagName) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Text("Show favorites only")
            Switch(
                checked = state.favoritesOnly,
                onCheckedChange = { vm.setFavoritesOnly(it) },
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "Total notes: ${state.totalCount}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val filteredNotes = remember(state) { vm.filteredNotes() }

            if (filteredNotes.isEmpty()) {
                Text(
                    "Список порожній. Додайте перший елемент.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                if (filteredNotes.size > 20) {
                    Text(
                        "Надто багато нотаток!",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredNotes) { note ->
                        NoteListItem(
                            note = note,
                            onClick = { onItemClick(note.id) },
                            onDelete = { vm.deleteNote(note.id) },
                            onToggleFavorite = { vm.toggleFavorite(note.id) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Нова нотатка") },
                text = {
                    InputPanel(
                        inputText = dialogTitle,
                        contentText = dialogContent,
                        onInputChange = { dialogTitle = it },
                        onContentChange = { dialogContent = it }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        vm.addNote(dialogTitle, dialogContent)
                        dialogTitle = ""
                        dialogContent = ""
                        showDialog = false
                    }) {
                        Text("Додати")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        dialogTitle = ""
                        dialogContent = ""
                    }) {
                        Text("Скасувати")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "NotesList Light")
@Composable
private fun NotesListLightPreview() {
    NoteAppTheme(darkTheme = false, dynamicColor = false) {
        NotesListScreen()
    }
}

@Preview(showBackground = true, name = "NotesList Dark")
@Composable
private fun NotesListDarkPreview() {
    NoteAppTheme(darkTheme = true, dynamicColor = false) {
        NotesListScreen()
    }
}

@Composable
fun InputPanel(
    inputText: String,
    contentText: String,
    onInputChange: (String) -> Unit,
    onContentChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            label = { Text("Назва нотатки") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contentText,
            onValueChange = onContentChange,
            label = { Text("Вміст нотатки") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}