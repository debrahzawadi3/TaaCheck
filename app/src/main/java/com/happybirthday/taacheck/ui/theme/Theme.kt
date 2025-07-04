package com.happybirthday.taacheck.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme

// Color definitions
val BluePrimary = Color(0xFF0D47A1)        // Deep blue
val BlueSecondary = Color(0xFF1976D2)      // Lighter blue for dark mode
val ButterYellow = Color(0xFFFFEDA8)       // Butter yellow
val SurfaceWhite = Color(0xFFFFFFFF)       // Clean white background
val TextBlack = Color(0xFF212121)          // Strong readable text
val ErrorRed = Color(0xFFB00020)           // Optional: error color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    secondary = ButterYellow,
    onSecondary = TextBlack,
    background = SurfaceWhite,
    onBackground = TextBlack,
    surface = SurfaceWhite,
    onSurface = TextBlack,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueSecondary,
    onPrimary = Color.White,
    secondary = ButterYellow,
    onSecondary = TextBlack,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun TaaCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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