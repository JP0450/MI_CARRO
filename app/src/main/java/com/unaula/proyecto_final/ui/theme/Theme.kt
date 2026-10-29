package com.unaula.proyecto_final.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = ColorDeepBlue,
    onPrimary = Color.White,
    primaryContainer = ColorBurgundy,
    onPrimaryContainer = ColorDeepGreen,
    secondary = ColorAzureBlue,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFFFBBF24),
    tertiaryContainer = Color(0xFF451A03),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = DarkBackground,
    onBackground = Color(0xFFF8EBF1),
    surface = DarkSurface,
    onSurface = Color(0xFFF8EBF1),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFE2D6DC),
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFEE2E2),
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ColorAzureBlue,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = ColorDeepGreen,
    secondary = ColorDeepBlue,
    secondaryContainer = ColorMintAqua,
    onSecondaryContainer = ColorTextPrimary,
    tertiary = Color(0xFFD97706),
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF92400E),
    background = ColorBlushPink,
    onBackground = ColorTextPrimary,
    surface = Color.White,
    onSurface = ColorTextPrimary,
    surfaceVariant = Color.White,
    onSurfaceVariant = ColorTextSecondary,
    error = Color(0xFFEF4444),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    outline = ColorBorder,
    outlineVariant = ColorBorderMint
)

@Composable
fun PROYECTO_FINALTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
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
