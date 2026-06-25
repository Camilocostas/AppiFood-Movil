// ui/theme/Theme.kt
package com.example.appifood_movil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary          = FoodPrimary,
    onPrimary        = Color.White,
    primaryContainer = FoodDeep,
    secondary        = FoodYellow,
    onSecondary      = Color(0xFF1A0000),
    tertiary         = FoodGreen,
    background       = DarkBackground,
    onBackground     = DarkTextPrimary,
    surface          = DarkSurface,
    onSurface        = DarkTextPrimary,
    surfaceVariant   = DarkSurfaceVar,
    onSurfaceVariant = DarkTextMuted,
    outline          = DarkBorder,
    error            = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary          = FoodPrimary,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFEBEB),
    secondary        = FoodYellow,
    onSecondary      = Color(0xFF1A0000),
    tertiary         = FoodGreen,
    background       = LightBackground,
    onBackground     = LightTextPrimary,
    surface          = LightSurface,
    onSurface        = LightTextPrimary,
    surfaceVariant   = LightSurfaceVar,
    onSurfaceVariant = LightTextMuted,
    outline          = LightBorder,
    error            = FoodPrimary
)

// ── Estado global del tema — accesible desde cualquier composable ─
data class ThemeState(
    val isDarkMode : Boolean,
    val toggle     : () -> Unit
)

val LocalThemeState = staticCompositionLocalOf<ThemeState> {
    error("ThemeState no provisto")
}

@Composable
fun AppifoodMovilTheme(
    darkTheme    : Boolean = isSystemInDarkTheme(),  // ← Cambio: ahora usa el sistema por defecto
    dynamicColor : Boolean = false,
    content      : @Composable () -> Unit
) {
    // Si darkTheme es null, usa el sistema
    var isDark by remember { mutableStateOf(darkTheme) }

    // ✅ Esto hace que el tema se actualice cuando cambia el sistema
    val currentIsDark = if (darkTheme) {
        // Si darkTheme es true, usa el modo oscuro siempre
        true
    } else {
        // Si es false, usa el modo claro siempre
        false
    }

    // Actualiza isDark cuando cambie el sistema
    LaunchedEffect(darkTheme) {
        isDark = darkTheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else   -> LightColorScheme
    }

    val themeState = ThemeState(
        isDarkMode = isDark,
        toggle     = { isDark = !isDark }  // El toggle manual también funciona
    )

    CompositionLocalProvider(LocalThemeState provides themeState) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}