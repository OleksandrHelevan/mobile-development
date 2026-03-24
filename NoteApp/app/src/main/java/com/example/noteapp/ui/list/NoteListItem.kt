package com.example.noteapp.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Note

@Composable
fun NoteListItem(
    note: Note,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = note.content)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Priority: ${note.priority}", style = MaterialTheme.typography.bodySmall)
                }

                Row {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (note.isFavorite) "Unfavorite" else "Favorite",
                            tint = if (note.isFavorite) Color.Yellow else Color.Gray
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}