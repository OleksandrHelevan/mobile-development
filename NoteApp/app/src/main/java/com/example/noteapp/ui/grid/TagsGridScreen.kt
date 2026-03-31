package com.example.noteapp.ui.grid

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.model.Tag
import com.example.noteapp.repository.RepositoryProvider
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
fun TagsGridScreen(
    onTagClick: (Tag) -> Unit = {}
) {
    val vm: TagsGridViewModel = viewModel(
        factory = TagsGridViewModelFactory(RepositoryProvider.notesRepository)
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    if (state.isVertical) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(state.tags) { tag ->
                TagGridItem(tag = tag, onClick = onTagClick)
            }
        }
    } else {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(state.tags) { tag ->
                TagGridItem(tag = tag, onClick = onTagClick)
            }
        }
    }
}

@Preview(
    name = "TagsGrid Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "TagsGrid Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun TagsGridPreview() {
    NoteAppTheme(dynamicColor = false) {
        TagsGridScreen()
    }
}