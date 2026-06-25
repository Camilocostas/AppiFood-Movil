// ui/screens/SettingsScreen.kt
package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.ui.theme.LocalThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    val themeState = LocalThemeState.current
    val isDark     = themeState.isDarkMode

    // Colores del tema activo
    val bg       = MaterialTheme.colorScheme.background
    val surface  = MaterialTheme.colorScheme.surface
    val red      = MaterialTheme.colorScheme.primary
    val onSurf   = MaterialTheme.colorScheme.onSurface
    val muted    = MaterialTheme.colorScheme.onSurfaceVariant

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    val fadeAlpha by animateFloatAsState(  // ← Cambio aquí: alpha → fadeAlpha
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label         = "settingsFade"
    )
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        fontWeight = FontWeight.ExtraBold,
                        color      = onSurf
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = onSurf)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .graphicsLayer { alpha = fadeAlpha }  // ← Cambio aquí: alpha → fadeAlpha
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Header de sección ─────────────────────────────────
            SettingsSectionHeader(title = "Apariencia", tint = onSurf)

            // ── Tarjeta de modo oscuro ────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(18.dp),
                colors    = CardDefaults.cardColors(containerColor = surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícono animado que cambia según el tema
                    val iconRotation by animateFloatAsState(
                        targetValue   = if (isDark) 180f else 0f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        label         = "iconRot"
                    )
                    Box(
                        modifier         = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(red.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = if (isDark)
                                Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint               = red,
                            modifier           = Modifier
                                .size(26.dp)
                                .graphicsLayer { rotationY = iconRotation }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Modo oscuro",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                            color      = onSurf
                        )
                        Text(
                            text     = if (isDark) "Activado" else "Desactivado",
                            fontSize = 13.sp,
                            color    = muted
                        )
                    }

                    // Switch con colores de marca
                    Switch(
                        checked         = isDark,
                        onCheckedChange = { themeState.toggle() },
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White,
                            checkedTrackColor   = red,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = muted.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            // ── Vista previa del tema activo ──────────────────────
            ThemePreviewCard(isDark = isDark, red = red, surface = surface, onSurf = onSurf)

            Spacer(modifier = Modifier.height(8.dp))

            // ── Otras configuraciones (stubs para crecer) ─────────
            SettingsSectionHeader(title = "Notificaciones", tint = onSurf)

            SettingsToggleRow(
                icon    = Icons.Default.Notifications,
                title   = "Notificaciones push",
                subtitle = "Recibe alertas de tus pedidos",
                surface = surface,
                red     = red,
                onSurf  = onSurf,
                muted   = muted
            )

            SettingsToggleRow(
                icon     = Icons.Default.Campaign,
                title    = "Promociones",
                subtitle = "Ofertas y descuentos exclusivos",
                surface  = surface,
                red      = red,
                onSurf   = onSurf,
                muted    = muted
            )

            SettingsSectionHeader(title = "Cuenta", tint = onSurf)

            SettingsLinkRow(
                icon    = Icons.Default.Security,
                title   = "Privacidad y seguridad",
                surface = surface,
                red     = red,
                onSurf  = onSurf
            )

            SettingsLinkRow(
                icon    = Icons.Default.Info,
                title   = "Acerca de AppiFood",
                surface = surface,
                red     = red,
                onSurf  = onSurf
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Versión de la app
            Text(
                "AppiFood v1.0.0",
                color    = muted,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// ThemePreviewCard — muestra cómo se ve el tema activo
// ─────────────────────────────────────────────────────────────────
@Composable
private fun ThemePreviewCard(
    isDark  : Boolean,
    red     : Color,
    surface : Color,
    onSurf  : Color
) {
    val previewBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)

    AnimatedContent(
        targetState  = isDark,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        label = "themePreview"
    ) { dark ->
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = previewBg),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text     = if (dark) "🌙 Modo oscuro activo" else "☀️ Modo claro activo",
                    color    = if (dark) Color(0xFFF1F1F1) else Color(0xFF1A1A1A),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Componentes de la pantalla de configuración
// ─────────────────────────────────────────────────────────────────
@Composable
private fun SettingsSectionHeader(title: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp).height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            title,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 16.sp,
            color      = tint
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon     : androidx.compose.ui.graphics.vector.ImageVector,
    title    : String,
    subtitle : String,
    surface  : Color,
    red      : Color,
    onSurf   : Color,
    muted    : Color
) {
    var checked by remember { mutableStateOf(true) }
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = red, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = onSurf)
                Text(subtitle, fontSize = 12.sp, color = muted)
            }
            Switch(
                checked         = checked,
                onCheckedChange = { checked = it },
                colors          = SwitchDefaults.colors(
                    checkedTrackColor = red,
                    checkedThumbColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun SettingsLinkRow(
    icon    : androidx.compose.ui.graphics.vector.ImageVector,
    title   : String,
    surface : Color,
    red     : Color,
    onSurf  : Color
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = red, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                color = onSurf, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = onSurf.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp))
        }
    }
}