package com.neverdid.outside.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = Lime,
    onPrimaryContainer = Forest,
    secondary = Sunset,
    onSecondary = Ink,
    secondaryContainer = SunsetSoft,
    onSecondaryContainer = Ink,
    tertiary = Lake,
    onTertiary = Color.White,
    tertiaryContainer = LakeSoft,
    onTertiaryContainer = Ink,
    background = Canvas,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE9EDE7),
    onSurfaceVariant = Muted,
    outline = Line,
)

private val DarkColors = darkColorScheme(
    primary = Lime,
    onPrimary = Forest,
    primaryContainer = ForestLight,
    onPrimaryContainer = LimeSoft,
    secondary = Sunset,
    tertiary = Color(0xFF8FD1DB),
    background = Color(0xFF101713),
    onBackground = Color(0xFFE9EEE9),
    surface = Color(0xFF18211D),
    onSurface = Color(0xFFE9EEE9),
    surfaceVariant = Color(0xFF26302B),
    onSurfaceVariant = Color(0xFFBEC9C3),
    outline = Color(0xFF46534D),
)

@Composable
fun OutsideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OutsideTypography,
        content = content,
    )
}
