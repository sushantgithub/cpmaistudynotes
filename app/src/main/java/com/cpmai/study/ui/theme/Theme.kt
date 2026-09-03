package com.cpmai.study.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF0E2144)
val NavyMid = Color(0xFF16305C)
val Saffron = Color(0xFFE8930C)
val Paper = Color(0xFFFBF7F0)
val Card = Color(0xFFFFFFFF)
val Ink = Color(0xFF1B2432)
val Muted = Color(0xFF5B6472)
val Teal = Color(0xFF0F766E)
val SoftSaffron = Color(0xFFFDF3E1)

private val LightColors = lightColorScheme(
    primary = NavyMid,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E2F5),
    secondary = Saffron,
    onSecondary = Navy,
    secondaryContainer = SoftSaffron,
    background = Paper,
    onBackground = Ink,
    surface = Card,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEEEAE2),
    onSurfaceVariant = Muted,
    outline = Color(0xFFD4CFC4),
    tertiary = Teal
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB9D0F5),
    onPrimary = Navy,
    secondary = Saffron,
    background = Color(0xFF0C1220),
    surface = Color(0xFF152036),
    onBackground = Color(0xFFE8EEF8),
    onSurface = Color(0xFFE8EEF8)
)

@Composable
fun CpmaiTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
