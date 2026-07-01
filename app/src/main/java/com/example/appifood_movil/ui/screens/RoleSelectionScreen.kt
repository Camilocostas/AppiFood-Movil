// ui/screens/RoleSelectionScreen.kt
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appifood_movil.navigation.Screen
import kotlinx.coroutines.delay

private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@Composable
fun RoleSelectionScreen(navController: NavController) {

    // ── Animación de entrada ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "roleFade"
    )
    val headerOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else (-30).dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label         = "roleHeaderSlide"
    )
    val cardsOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 50.dp,
        animationSpec = tween(600, delayMillis = 150, easing = FastOutSlowInEasing),
        label         = "roleCardsSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    // Estado de selección para efecto visual
    var selectedRole by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(RedPrimary, RedDark, RedDeep))
            )
            .graphicsLayer { alpha = screenAlpha }
    ) {
        // Círculos decorativos — sistema de diseño unificado
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.size(280.dp).offset(x = (-80).dp, y = (-60).dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier.size(200.dp).align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = 40.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.04f))
            )
            Box(
                modifier = Modifier.size(160.dp).align(Alignment.BottomCenter)
                    .offset(y = 60.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.03f))
            )
        }

        // ✅ Usamos un Column sin scroll para que el fondo blanco ocupe toda la altura
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .offset(y = headerOffsetY)
                    .padding(top = 64.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo halo
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier.size(90.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Text(
                        "AppiFood",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "¿Cómo quieres usar\nAppiFood?",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                    textAlign  = TextAlign.Center,
                    lineHeight = 33.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Línea acento amarilla
                Box(
                    modifier = Modifier.width(48.dp).height(3.dp)
                        .clip(RoundedCornerShape(50)).background(YellowAccent)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Elige tu perfil para continuar",
                    fontSize  = 15.sp,
                    color     = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            // ── Tarjetas de roles ─────────────────────────────────
            // ✅ Usamos un Box con un Column scrollable dentro para que el fondo blanco ocupe todo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // ✅ Ocupa todo el espacio restante
                    .offset(y = cardsOffsetY)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                // ✅ Column con scroll para el contenido
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 32.dp, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Selecciona tu tipo de cuenta",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 18.sp,
                        color      = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ── Card Cliente ──────────────────────────────────
                    RoleCard(
                        emoji       = "🍔",
                        title       = "Soy cliente",
                        subtitle    = "Quiero pedir comida",
                        description = "Explora restaurantes, ordena tus platos favoritos, " +
                                "rastrea tus pedidos en tiempo real y disfruta " +
                                "ofertas exclusivas en Popayán.",
                        features    = listOf(
                            "Explora restaurantes cercanos",
                            "Pide tus platos favoritos",
                            "Rastrea pedidos en tiempo real",
                            "Ofertas y descuentos exclusivos"
                        ),
                        accentColor   = RedPrimary,
                        isSelected    = selectedRole == "cliente",
                        animDelay     = 0,
                        onSelect      = {
                            selectedRole = "cliente"
                        },
                        onContinue    = {
                            // ✅ Cambio: ahora solo navega sin popUpTo
                            navController.navigate(Screen.Auth.route)
                        }
                    )

                    // ── Card Restaurante ──────────────────────────────
                    RoleCard(
                        emoji       = "🏪",
                        title       = "Soy restaurante",
                        subtitle    = "Quiero administrar mi negocio",
                        description = "Gestiona tu menú, fotos, pedidos entrantes, " +
                                "usuarios y estadísticas de tu restaurante " +
                                "desde un panel completo.",
                        features    = listOf(
                            "Administra tu menú y precios",
                            "Gestiona pedidos en tiempo real",
                            "Sube fotos de tu restaurante",
                            "Estadísticas y reportes"
                        ),
                        accentColor   = RedPrimary,
                        isSelected    = selectedRole == "restaurante",
                        animDelay     = 80,
                        onSelect      = {
                            selectedRole = "restaurante"
                        },
                        onContinue    = {
                            // ✅ Cambio: ahora solo navega sin popUpTo
                            navController.navigate(Screen.RestaurantAuth.route)
                        }
                    )

                    // ✅ Spacer para dar espacio adicional al final
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// RoleCard — tarjeta premium de selección de rol
// ─────────────────────────────────────────────────────────────────
@Composable
private fun RoleCard(
    emoji       : String,
    title       : String,
    subtitle    : String,
    description : String,
    features    : List<String>,
    accentColor : Color,
    isSelected  : Boolean,
    animDelay   : Int,
    onSelect    : () -> Unit,
    onContinue  : () -> Unit
) {
    // Animación de entrada individual con delay
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(animDelay.toLong())
        cardVisible = true
    }

    // Escala al seleccionar
    val cardScale by animateFloatAsState(
        targetValue   = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "cardScale_$title"
    )

    // Color del borde animado
    val borderColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else Color(0xFFE0E0E0),
        animationSpec = tween(250),
        label         = "border_$title"
    )

    // Altura del contenido expandible
    val expandedAlpha by animateFloatAsState(
        targetValue   = if (isSelected) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label         = "expand_$title"
    )

    AnimatedVisibility(
        visible = cardVisible,
        enter   = fadeIn(tween(350)) + slideInVertically(
            animationSpec  = tween(400, easing = FastOutSlowInEasing),
            initialOffsetY = { it / 2 }
        )
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .scale(cardScale)
                .clickable { onSelect() },
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            border    = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 6.dp else 2.dp
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ── Banda de color superior ───────────────────────
                AnimatedVisibility(visible = isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(accentColor, accentColor.copy(alpha = 0.4f))
                                )
                            )
                    )
                }

                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícono con fondo de color
                    Box(
                        modifier         = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 17.sp,
                            color      = TextPrimary
                        )
                        Text(
                            subtitle,
                            fontSize = 13.sp,
                            color    = Color(0xFF666666)
                        )
                    }

                    // Indicador de selección
                    AnimatedVisibility(
                        visible     = isSelected,
                        enter       = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                        exit        = scaleOut() + fadeOut()
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check, null,
                                tint     = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // ── Descripción + features (visibles al seleccionar) ──
                AnimatedVisibility(
                    visible     = isSelected,
                    enter       = expandVertically(tween(300)) + fadeIn(tween(250)),
                    exit        = shrinkVertically(tween(250)) + fadeOut(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = expandedAlpha }
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    ) {
                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            description,
                            fontSize   = 13.sp,
                            color      = Color(0xFF555555),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Features
                        features.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier          = Modifier.padding(vertical = 3.dp)
                            ) {
                                Box(
                                    modifier         = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(accentColor.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check, null,
                                        tint     = accentColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(feature, fontSize = 13.sp, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón continuar
                        Button(
                            onClick   = onContinue,
                            modifier  = Modifier.fillMaxWidth().height(50.dp),
                            shape     = RoundedCornerShape(14.dp),
                            colors    = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor   = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(
                                "Continuar como ${if (title.contains("cliente")) "cliente" else "restaurante"}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.ArrowForward, null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}