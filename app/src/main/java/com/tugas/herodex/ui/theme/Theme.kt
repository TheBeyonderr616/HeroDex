package com.tugas.herodex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HeroDexColorScheme = darkColorScheme(
    primary = MarvelRed,
    secondary = MarvelDarkBlue,
    background = DeepBlack,
    surface = MarvelCardRed,
    onPrimary = WhiteText
)

@Composable
fun HeroDexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HeroDexColorScheme,
        typography = AppTypography,
        content = content
    )
}