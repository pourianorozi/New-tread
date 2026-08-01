package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnDarkTextPrimary,
    primaryContainer = PrimaryBlueVariant,
    secondary = SecondaryCyan,
    onSecondary = DarkBackground,
    tertiary = TertiaryGold,
    background = DarkBackground,
    onBackground = OnDarkTextPrimary,
    surface = DarkSurface,
    onSurface = OnDarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkTextSecondary,
    outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnDarkTextPrimary,
    secondary = SecondaryCyan,
    background = LightBackground,
    onBackground = DarkBackground,
    surface = LightSurface,
    onSurface = DarkBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = OnDarkTextMuted,
    outline = LightCardBorder
)

@Composable
fun SmartTradeTheme(
    darkTheme: Boolean = true, // Default to dark theme for professional trading vibe
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Enforce RTL for Persian UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
