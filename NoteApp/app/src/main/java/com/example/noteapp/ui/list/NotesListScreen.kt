package com.example.noteapp.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.di.ServiceLocator
import com.example.noteapp.ui.details.NoteDetailsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onAddOrEditClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val vm: NotesListViewModel = viewModel(
        factory = NotesListViewModelFactory(
            repository = ServiceLocator.notesRepository(context),
            settingsRepository = ServiceLocator.settingsRepository(context)
        )
    )
    val state by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded
    var selectedNoteIdForPane by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.networkError) {
        state.networkError?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        // Ігноруємо системні відступи для максимального простору
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        // Використовуємо paddingValues через consumeWindowInsets або просто Modifier.padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Це прибирає попередження і враховує Scaffold (якщо треба)
                .padding(top = 4.dp, start = 8.dp, end = 8.dp)
        ) {
            if (state.isOffline) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Офлайн", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = { vm.syncWithNetwork() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Повторити", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredNotes = remember(state) { vm.filteredNotes() }

                val headerContent = @Composable {
                    Column(modifier = Modifier.padding(bottom = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = {
                                    if (isExpanded) selectedNoteIdForPane =
                                        "new" else onAddOrEditClick("new")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("+ Нотатка", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp)
                        ) {
                            state.availableTags.forEach { tagName ->
                                FilterChip(
                                    selected = state.selectedTag == tagName,
                                    onClick = { vm.selectTag(if (state.selectedTag == tagName) null else tagName) },
                                    label = {
                                        Text(
                                            tagName,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .height(28.dp)
                                )
                            }
                        }


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Тільки улюблені", style = MaterialTheme.typography.labelSmall)
                            Switch(
                                checked = state.favoritesOnly,
                                onCheckedChange = { vm.setFavoritesOnly(it) },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .graphicsLayer(scaleX = 0.7f, scaleY = 0.7f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Усього: ${state.totalCount}",
                                style = MaterialTheme.typography.labelSmall
                            )

                        }
                    }
                }

                val listContent = @Composable {
                    if (filteredNotes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Порожньо", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items = filteredNotes, key = { it.id }) { note ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = {
                                        if (it == SwipeToDismissBoxValue.EndToStart) {
                                            vm.deleteNote(note.id); true
                                        } else false
                                    }
                                )
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    backgroundContent = {
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 20.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(Icons.Default.Delete, null, tint = Color.White)
                                        }
                                    }
                                ) {
                                    NoteListItem(
                                        note = note,
                                        markdownEnabled = state.markdownEnabled,
                                        onClick = {
                                            if (isExpanded) selectedNoteIdForPane =
                                                note.id else onAddOrEditClick(note.id)
                                        },
                                        onDelete = { vm.deleteNote(note.id) },
                                        onToggleFavorite = { vm.toggleFavorite(note.id) }
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                    }
                }

                if (isExpanded) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 4.dp)) {
                            headerContent()
                            Box(modifier = Modifier.weight(1f)) { listContent() }
                        }
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Box(modifier = Modifier
                            .weight(1.8f)
                            .fillMaxHeight()) {
                            selectedNoteIdForPane?.let { id ->
                                NoteDetailsScreen(
                                    noteId = id,
                                    widthSizeClass = widthSizeClass,
                                    onBack = { selectedNoteIdForPane = null })
                            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Оберіть нотатку", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        headerContent()
                        Box(modifier = Modifier.weight(1f)) { listContent() }
                    }
                }
            }
        }
    }
}