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
    primary = RadiantGold,
    onPrimary = RoyalNavyDark,
    primaryContainer = RoyalNavy,
    onPrimaryContainer = RadiantGoldLight,
    secondary = RadiantGoldDark,
    onSecondary = Color.White,
    secondaryContainer = RoyalNavyLight,
    onSecondaryContainer = Color.White,
    tertiary = RadiantGoldLight,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = RoyalNavyLight
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RoyalNavy,
    onPrimary = Color.White,
    primaryContainer = RoyalNavyLight,
    onPrimaryContainer = Color.White,
    secondary = RadiantGold,
    onSecondary = RoyalNavyDark,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = RoyalNavyDark,
    tertiary = RadiantGoldDark,
    background = ChurchBackgroundLight,
    onBackground = ChurchTextPrimary,
    surface = ChurchWhite,
    onSurface = ChurchTextPrimary,
    surfaceVariant = ChurchSurfaceVariant,
    onSurfaceVariant = ChurchTextSecondary,
    outline = ChurchBorder
  )

@Composable
fun ChurchTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

