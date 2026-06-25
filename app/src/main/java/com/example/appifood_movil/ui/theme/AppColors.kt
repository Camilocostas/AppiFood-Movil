// ui/theme/AppColors.kt
package com.example.appifood_movil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Objeto de acceso a colores semánticos ─────────────────────────
// En lugar de hardcodear Color(0xFF...) en cada pantalla,
// usa AppColors.textPrimary, AppColors.surface, etc.
// Estos responden automáticamente al tema claro/oscuro.
object AppColors {

    val redPrimary   : Color @Composable get() = MaterialTheme.colorScheme.primary
    val yellowAccent : Color @Composable get() = MaterialTheme.colorScheme.secondary
    val successGreen : Color @Composable get() = MaterialTheme.colorScheme.tertiary

    val background   : Color @Composable get() = MaterialTheme.colorScheme.background
    val surface      : Color @Composable get() = MaterialTheme.colorScheme.surface
    val surfaceVar   : Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant

    val textPrimary  : Color @Composable get() = MaterialTheme.colorScheme.onSurface
    val textMuted    : Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val border       : Color @Composable get() = MaterialTheme.colorScheme.outline

    // Constantes de marca que nunca cambian con el tema
    val AppiFoodRed  : Color = FoodPrimary
}