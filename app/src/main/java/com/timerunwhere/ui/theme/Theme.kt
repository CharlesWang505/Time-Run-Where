package com.timerunwhere.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkScheme: ColorScheme = darkColorScheme(
    primary = ProductiveCyan,
    secondary = TimeSinkPink,
    background = PureBlack,
    surface = Panel,
    onPrimary = PureBlack,
    onSecondary = SoftWhite,
    onBackground = SoftWhite,
    onSurface = SoftWhite
)

@Composable
fun TimeRunWhereTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
