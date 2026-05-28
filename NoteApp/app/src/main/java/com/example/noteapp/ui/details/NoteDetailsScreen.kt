package com.example.noteapp.ui.details

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.noteapp.di.ServiceLocator
import java.io.File

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
                        modifier = Modifier.padding(end = 8.dp).testTag("SaveButton")
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
                        HardwareIntegrationSection(state, vm)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AnimatedAdditionalInfoSection(state, vm, focusManager, categories, expandedCategory) { expandedCategory = it }
                    }
                }
            } else {
                BasicInfoSection(state, vm, focusManager)
                HardwareIntegrationSection(state, vm)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AnimatedAdditionalInfoSection(state, vm, focusManager, categories, expandedCategory) { expandedCategory = it }
            }
        }
    }
}

@Composable
fun HardwareIntegrationSection(state: FormState, vm: NoteDetailsViewModel) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            vm.saveImageToInternalStorage(context, bitmap)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            openAppSettings(context)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            vm.fetchLocation(context)
        } else {
            openAppSettings(context)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Медіа та Локація", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            if (state.imagePath != null) {
                AsyncImage(
                    model = File(state.imagePath),
                    contentDescription = "Фото нотатки",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Button(
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.imagePath == null) "Додати фото з камери" else "Оновити фото")
            }

            HorizontalDivider()

            if (state.latitude != null && state.longitude != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Широта: ${state.latitude}", style = MaterialTheme.typography.bodyMedium)
                    Text("Довгота: ${state.longitude}", style = MaterialTheme.typography.bodyMedium)
                    state.locationAccuracy?.let {
                        Text("Точність: $it метрів", style = MaterialTheme.typography.bodySmall)
                    }
                    state.distanceToKyiv?.let {
                        val km = it / 1000
                        Text("Відстань до центру Києва: ${String.format("%.2f", km)} км", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Button(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.latitude == null) "Отримати геолокацію" else "Оновити геолокацію")
            }
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
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
            .testTag("TitleField")
    )

    OutlinedTextField(
        value = state.content,
        onValueChange = vm::updateContent,
        label = { Text("Опис") },
        minLines = 3,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ContentField")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedAdditionalInfoSection(
    state: FormState, vm: NoteDetailsViewModel, focusManager: androidx.compose.ui.focus.FocusManager,
    categories: List<String>, expandedCategory: Boolean, onCategoryExpandChange: (Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrowRotation")
    val containerColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        label = "bgColor"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
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
                    modifier = Modifier.rotate(rotation)
                )
            }

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