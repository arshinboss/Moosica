package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VintageColorScheme = darkColorScheme(
    primary = AmberGlow,
    onPrimary = MahoganyDark,
    primaryContainer = MahoganyMedium,
    onPrimaryContainer = BrassGoldLight,
    secondary = BrassGold,
    onSecondary = MahoganyDark,
    secondaryContainer = MahoganyLight,
    onSecondaryContainer = CreamParchment,
    tertiary = WarmBronze,
    onTertiary = CreamParchment,
    background = BakeliteBlack,
    onBackground = CreamParchment,
    surface = MahoganyDark,
    onSurface = CreamParchment,
    surfaceVariant = MahoganyMedium,
    onSurfaceVariant = CreamMuted,
    outline = BrassDark,
    outlineVariant = BrushedAluminumLight
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = VintageColorScheme,
        typography = RetroTypography,
        content = content
    )
}
