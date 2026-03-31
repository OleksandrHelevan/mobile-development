package com.example.noteapp.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteapp.ui.theme.NoteAppTheme

@Composable
fun ProfileScreen(name: String, onNameChange: (String) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Інформація про додаток", style = MaterialTheme.typography.titleLarge)
        Text("Назва: NoteApp")
        Text("Версія: 1.0.5")
        Text("Розробник: Олександр")

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Ваше ім'я") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, name = "Profile Light")
@Composable
private fun ProfileLightPreview() {
    NoteAppTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen(name = "Олександр", onNameChange = {})
    }
}

@Preview(showBackground = true, name = "Profile Dark")
@Composable
private fun ProfileDarkPreview() {
    NoteAppTheme(darkTheme = true, dynamicColor = false) {
        ProfileScreen(name = "Олександр", onNameChange = {})
    }
}