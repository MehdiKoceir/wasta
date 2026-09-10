package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = SleekPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = SleekDarkContainer,
    onPrimaryContainer = SleekDarkText,
    secondary = SleekPrimary,
    onSecondary = Color.White,
    tertiary = SleekPrimaryContainer,
    background = SleekDarkBg,
    surface = SleekDarkSurface,
    onBackground = SleekDarkText,
    onSurface = SleekDarkText,
    surfaceVariant = SleekDarkContainer,
    onSurfaceVariant = SleekDarkTextSecondary,
    outline = SleekDarkBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SleekPrimary,
    onPrimary = Color.White,
    primaryContainer = SleekPrimaryContainer,
    onPrimaryContainer = OnSleekPrimaryContainer,
    secondary = SleekPrimary,
    onSecondary = Color.White,
    secondaryContainer = SleekPrimaryContainer,
    onSecondaryContainer = SleekNavy,
    background = SleekBackground,
    surface = SleekSurface,
    surfaceVariant = SleekContainer,
    onBackground = SleekTextPrimary,
    onSurface = SleekTextPrimary,
    onSurfaceVariant = SleekTextSecondary,
    outline = SleekBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun WastaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  MyApplicationTheme(darkTheme = darkTheme, content = content)
}

