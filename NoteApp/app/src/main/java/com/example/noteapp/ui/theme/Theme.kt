package com.example.noteapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandIndigo80,
    onPrimary = BrandIndigo20,
    primaryContainer = BrandIndigo20,
    onPrimaryContainer = BrandIndigo80,
    secondary = BrandIndigo80,
    onSecondary = BrandIndigo20,
    background = SurfaceDark,
    onBackground = BrandIndigo80,
    surface = SurfaceDark,
    onSurface = BrandIndigo80,
    onSurfaceVariant = Color(0xFFBFC4D5),
    outline = Color(0xFF3D4458),
    error = ErrorDark,
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandIndigo20,
    onPrimary = Color.White,
    primaryContainer = BrandIndigo80,
    onPrimaryContainer = BrandIndigo20,
    secondary = BrandIndigo20,
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = Color(0xFF1B1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1B1B1F),
    onSurfaceVariant = Color(0xFF4A4A57),
    outline = Color(0xFF7A7A90),
    error = ErrorLight,
    onError = Color.White
)

@Composable
fun NoteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}