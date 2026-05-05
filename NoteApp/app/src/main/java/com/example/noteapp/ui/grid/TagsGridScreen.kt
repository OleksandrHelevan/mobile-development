package com.example.noteapp.ui.grid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.di.ServiceLocator
import com.example.noteapp.data.Tag
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
fun TagsGridScreen(
    widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onTagClick: (Tag) -> Unit = {}
) {
    val context = LocalContext.current
    val vm: TagsGridViewModel = viewModel(
        factory = TagsGridViewModelFactory(ServiceLocator.notesRepository(context))
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    val columnsCount = if (widthSizeClass == WindowWidthSizeClass.Expanded) 4 else 2

    if (state.isVertical) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(state.tags) { tag ->
                TagGridItem(tag = tag, onClick = onTagClick)
            }
        }
    } else {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(columnsCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(state.tags) { tag ->
                TagGridItem(tag = tag, onClick = onTagClick)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagsGridPreview() {
    NoteAppTheme(dynamicColor = false) {
        TagsGridScreen()
    }
}