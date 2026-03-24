package com.example.noteapp.ui.grid

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Tag
import com.example.noteapp.repository.NotesRepository

@Composable
fun TagsGridScreen(
    isVertical: Boolean = true,
    onTagClick: (Tag) -> Unit = {}
) {
    if (isVertical) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(NotesRepository.tags.toList()) { tag ->
                TagGridItem(tag = tag)
            }
        }
    } else {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(NotesRepository.tags.toList()) { tag ->
                TagGridItem(tag = tag)
            }
        }
    }
}