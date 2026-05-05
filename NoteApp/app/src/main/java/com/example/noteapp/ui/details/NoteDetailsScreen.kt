package com.example.noteapp.ui.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.di.ServiceLocator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    noteId: String?,
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vm: NoteDetailsViewModel = viewModel(
        key = noteId ?: "new",
        factory = NoteDetailsViewModel.Factory(noteId, ServiceLocator.notesRepository(context))
    )
    val state by vm.formState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val categories = listOf("Особисте", "Робота", "Навчання", "Інше")
    var expandedCategory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == "new" || noteId == null) "Нова нотатка" else "Редагування") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") }
                },
                actions = {
                    Button(
                        onClick = { vm.saveNote(onBack) },
                        enabled = state.isValid,
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text("Зберегти") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded

            if (isExpanded) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        BasicInfoSection(state, vm, focusManager)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // ЗАВДАННЯ 2: Анімована розгортувана секція
                        AnimatedAdditionalInfoSection(state, vm, focusManager, categories, expandedCategory) { expandedCategory = it }
                    }
                }
            } else {
                BasicInfoSection(state, vm, focusManager)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                // ЗАВДАННЯ 2: Анімована розгортувана секція
                AnimatedAdditionalInfoSection(state, vm, focusManager, categories, expandedCategory) { expandedCategory = it }
            }
        }
    }
}

@Composable
fun BasicInfoSection(state: FormState, vm: NoteDetailsViewModel, focusManager: androidx.compose.ui.focus.FocusManager) {
    Text("Основна інформація", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

    OutlinedTextField(
        value = state.title,
        onValueChange = vm::updateTitle,
        label = { Text("Заголовок") },
        isError = state.titleError != null,
        supportingText = { state.titleError?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (!it.isFocused) vm.validateTitle() }
    )

    OutlinedTextField(
        value = state.content,
        onValueChange = vm::updateContent,
        label = { Text("Опис") },
        minLines = 3,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedAdditionalInfoSection(
    state: FormState, vm: NoteDetailsViewModel, focusManager: androidx.compose.ui.focus.FocusManager,
    categories: List<String>, expandedCategory: Boolean, onCategoryExpandChange: (Boolean) -> Unit
) {
    // ЗАВДАННЯ 2: Стан розгорнутості
    var isExpanded by remember { mutableStateOf(false) }

    // Анімація кута повороту стрілки (180 градусів)
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrowRotation")
    // Анімація зміни кольору фону секції
    val containerColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        label = "bgColor"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Клікабельний заголовок (Header)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Додаткові параметри",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Розгорнути",
                    modifier = Modifier.rotate(rotation) // Застосування анімації повороту
                )
            }

            // ЗАВДАННЯ 2: Анімація висоти та видимості контенту
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.sourceUrl,
                        onValueChange = vm::updateUrl,
                        label = { Text("URL джерела (опціонально)") },
                        isError = state.urlError != null,
                        supportingText = { state.urlError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { if (!it.isFocused) vm.validateUrl() }
                    )

                    OutlinedTextField(
                        value = state.estimatedTime,
                        onValueChange = vm::updateTime,
                        label = { Text("Очікуваний час (хв)") },
                        isError = state.timeError != null,
                        supportingText = { state.timeError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { if (!it.isFocused) vm.validateTime() }
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = onCategoryExpandChange,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Категорія") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                            isError = state.categoryError != null,
                            supportingText = { state.categoryError?.let { Text(it) } },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedCategory, onDismissRequest = { onCategoryExpandChange(false) }) {
                            categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        vm.updateCategory(selectionOption)
                                        onCategoryExpandChange(false)
                                        vm.validateCategory()
                                    }
                                )
                            }
                        }
                    }

                    Column {
                        Text("Пріоритет: ${state.priority.toInt()}")
                        Slider(
                            value = state.priority,
                            onValueChange = vm::updatePriority,
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Додати в улюблені", modifier = Modifier.weight(1f))
                        Switch(checked = state.isFavorite, onCheckedChange = vm::toggleFavorite)
                    }
                }
            }
        }
    }
}