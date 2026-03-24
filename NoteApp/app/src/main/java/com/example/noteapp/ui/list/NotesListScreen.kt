package com.example.noteapp.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Note
import com.example.noteapp.repository.NotesRepository
import kotlinx.coroutines.delay

@Composable
@Preview(showBackground = false)
fun NotesListScreen() {
    var notes by remember { mutableStateOf(emptyList<Note>()) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var showFavorites by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        delay(1500L)
        notes = NotesRepository.notes
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("+ Додати нотатку")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            NotesRepository.tags.forEach { tag ->
                FilterChip(
                    selected = selectedTag == tag.name,
                    onClick = { selectedTag = if (selectedTag == tag.name) null else tag.name },
                    label = { Text(tag.name) },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Text("Show favorites only")
            Switch(
                checked = showFavorites,
                onCheckedChange = { showFavorites = it },
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "Total notes: ${notes.size}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val filteredNotes by remember(selectedTag, showFavorites, notes) {
                derivedStateOf {
                    notes.filter { note ->
                        (!showFavorites || note.isFavorite) &&
                                (selectedTag == null || NotesRepository.noteCategory[note.title] == selectedTag)
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                Text(
                    "Список порожній. Додайте перший елемент.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                if (filteredNotes.size > 20) {
                    Text(
                        "Надто багато нотаток!",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredNotes) { note ->
                        NoteListItem(
                            note = note,
                            onDelete = { notes = notes - note },
                            onToggleFavorite = {
                                notes = notes.map {
                                    if (it.id == note.id) it.copy(isFavorite = !it.isFavorite) else it
                                }
                            }
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
                        if (dialogTitle.isNotBlank()) {
                            notes = notes + Note(
                                id = System.currentTimeMillis().toString(),
                                title = dialogTitle,
                                content = dialogContent,
                                priority = 1,
                                isFavorite = false
                            )
                            dialogTitle = ""
                            dialogContent = ""
                            showDialog = false
                        }
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