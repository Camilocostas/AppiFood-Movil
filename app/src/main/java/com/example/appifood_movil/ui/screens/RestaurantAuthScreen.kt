// ui/screens/RestaurantAuthScreen.kt
package com.example.appifood_movil.ui.screens

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.RestaurantAuthViewModel

// ── Paleta — misma del sistema de diseño ─────────────────────────
// ── Paleta — ROJO AppiFood ─────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)   // ✅ NUEVO
private val RedDark      = Color(0xFFB71C1C)   // ✅ NUEVO
private val RedDeep      = Color(0xFF7F0000)   // ✅ NUEVO
private val YellowAccent = Color(0xFFFFD600)   // ✅ NUEVO
private val FieldBg      = Color(0xFFF7F7F7)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)
enum class RestaurantAuthState { LOGIN, REGISTER }

@Composable
fun RestaurantAuthScreen(
    navController : NavController,
    viewModel     : RestaurantAuthViewModel = hiltViewModel()
) {
    var screenState by remember { mutableStateOf(RestaurantAuthState.LOGIN) }
    val isLoading   by viewModel.isLoading.collectAsState()
    val error       by viewModel.error.collectAsState()
    val isSuccess   by viewModel.isSuccess.collectAsState()

    // Busca el LaunchedEffect(isSuccess) y reemplázalo:
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            // ✅ popUpTo(0) limpia TODO el backstack
            // RestaurantDashboard queda como única pantalla en el stack
            navController.navigate(Screen.RestaurantDashboard.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // ── Animación de entrada ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "restAuthFade"
    )
    val headerOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else (-30).dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label         = "restHeaderSlide"
    )
    val cardOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 40.dp,
        animationSpec = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
        label         = "restCardSlide"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(RedPrimary, RedDark, RedDeep))
            )
            .graphicsLayer { alpha = screenAlpha }
    ) {
        // Círculos decorativos
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.size(260.dp).offset(x = (-80).dp, y = (-60).dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier.size(180.dp).align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = 40.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.04f))
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
                    .padding(top = 56.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón volver
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.ArrowBack, "Volver",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier.size(90.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Text("🏪", fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    if (screenState == RestaurantAuthState.LOGIN)
                        "Portal Restaurantes" else "Registra tu\nrestaurante",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                    textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.width(40.dp).height(3.dp)
                        .clip(RoundedCornerShape(50)).background(YellowAccent)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.18f)) {
                    Text(
                        text = if (screenState == RestaurantAuthState.LOGIN)
                            "🔐 Accede a tu panel" else "🎉 Crea tu cuenta",
                        color      = Color.White,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }
            }

            // ── Tarjeta blanca ────────────────────────────────────
            // ✅ Usamos un Box con weight(1f) para que ocupe todo el espacio restante
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // ✅ Ocupa todo el espacio restante
                    .offset(y = cardOffsetY)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
            ) {
                // ✅ Column con scroll para el contenido
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp, bottom = 40.dp)
                ) {
                    // Acento amarillo + título
                    Box(
                        modifier = Modifier.width(40.dp).height(3.dp)
                            .clip(RoundedCornerShape(50)).background(YellowAccent)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (screenState == RestaurantAuthState.LOGIN)
                            "¡Bienvenido de nuevo!" else "Crear cuenta de restaurante",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextPrimary
                    )

                    // Error
                    if (!error.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFEBEB)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Warning, null,
                                    tint = RedPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(error ?: "", color = RedPrimary, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    when (screenState) {
                        RestaurantAuthState.LOGIN    -> RestaurantLoginForm(
                            viewModel    = viewModel,
                            onRegSwitch  = { screenState = RestaurantAuthState.REGISTER }
                        )
                        RestaurantAuthState.REGISTER -> RestaurantRegisterForm(
                            viewModel    = viewModel,
                            onLoginSwitch = { screenState = RestaurantAuthState.LOGIN }
                        )
                    }

                    // ✅ Spacer para dar espacio adicional al final
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = YellowAccent, strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// RestaurantLoginForm
// ─────────────────────────────────────────────────────────────────
@Composable
private fun RestaurantLoginForm(
    viewModel   : RestaurantAuthViewModel,
    onRegSwitch : () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column {
        RestaurantTextField(
            label = "Correo electrónico", placeholder = "restaurante@email.com",
            icon = Icons.Default.Email, value = email, onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.height(14.dp))
        RestaurantTextField(
            label = "Contraseña", placeholder = "••••••••",
            icon = Icons.Default.Lock, isPassword = true,
            value = password, onValueChange = { password = it }
        )
        Spacer(modifier = Modifier.height(24.dp))
        RestaurantButton(
            text    = "Iniciar sesión",
            color   = RedPrimary,
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            viewModel.loginRestaurant(email.trim(), password)
        }
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            onClick  = onRegSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = TextMuted)) { append("¿No tienes cuenta? ") }
                withStyle(SpanStyle(color = RedPrimary, fontWeight = FontWeight.Bold)) {
                    append("Registra tu restaurante")
                }
            })
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// RestaurantRegisterForm — con los campos de la imagen
// ─────────────────────────────────────────────────────────────────
@Composable
private fun RestaurantRegisterForm(
    viewModel     : RestaurantAuthViewModel,
    onLoginSwitch : () -> Unit
) {
    var ownerName        by remember { mutableStateOf("") }
    var restaurantName   by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var phone            by remember { mutableStateOf("") }
    var documentNumber   by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }

    val isPasswordValid     = password.length >= 8
    val doPasswordsMatch    = password == confirmPassword
    val isFormValid         = ownerName.isNotBlank() && restaurantName.isNotBlank() &&
            email.isNotBlank() && phone.isNotBlank() && documentNumber.isNotBlank() &&
            isPasswordValid && doPasswordsMatch

    Column {
        RestaurantTextField(
            label = "Nombre del responsable", placeholder = "Ej: Camilo Acosta",
            icon = Icons.Default.Person, value = ownerName,
            onValueChange = { ownerName = it }
        )
        Spacer(modifier = Modifier.height(12.dp))
        RestaurantTextField(
            label = "Nombre del restaurante", placeholder = "Ej: Sabor del Valle",
            icon = Icons.Default.Restaurant, value = restaurantName,
            onValueChange = { restaurantName = it }
        )
        Spacer(modifier = Modifier.height(12.dp))
        RestaurantTextField(
            label = "Correo electrónico", placeholder = "restaurante@email.com",
            icon = Icons.Default.Email, value = email, onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.height(12.dp))
        RestaurantTextField(
            label = "Teléfono", placeholder = "3001234567",
            icon = Icons.Default.Phone, value = phone, onValueChange = { phone = it }
        )
        Spacer(modifier = Modifier.height(12.dp))
        RestaurantTextField(
            label = "Número de documento", placeholder = "123456789",
            icon = Icons.Default.Badge, value = documentNumber,
            onValueChange = { documentNumber = it }
        )
        Spacer(modifier = Modifier.height(12.dp))
        RestaurantTextField(
            label = "Contraseña", placeholder = "Mínimo 8 caracteres",
            icon = Icons.Default.Lock, isPassword = true,
            value = password, onValueChange = { password = it }
        )
        if (password.isNotBlank() && !isPasswordValid) {
            Text("⚠️ Mínimo 8 caracteres", color = RedPrimary, fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 3.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        RestaurantTextField(
            label = "Confirmar contraseña", placeholder = "Repite tu contraseña",
            icon = Icons.Default.Lock, isPassword = true,
            value = confirmPassword, onValueChange = { confirmPassword = it }
        )
        if (confirmPassword.isNotBlank() && !doPasswordsMatch) {
            Text("⚠️ Las contraseñas no coinciden", color = RedPrimary, fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 3.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        RestaurantButton(
            text    = "🏪  Registrar mi restaurante",
            color   = RedPrimary,
            enabled = isFormValid
        ) {
            viewModel.registerRestaurant(
                ownerName      = ownerName.trim(),
                restaurantName = restaurantName.trim(),
                email          = email.trim(),
                phone          = phone.trim(),
                documentNumber = documentNumber.trim(),
                password       = password
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Esta vista solo crea cuentas con rol restaurante.",
            color    = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick  = onLoginSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = TextMuted)) { append("¿Quieres registro normal? ") }
                withStyle(SpanStyle(color = RedPrimary, fontWeight = FontWeight.Bold)) {
                    append("Ir a registro de usuario")
                }
            })
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Componentes privados reutilizables
// ─────────────────────────────────────────────────────────────────
@Composable
private fun RestaurantTextField(
    label         : String,
    placeholder   : String,
    icon          : androidx.compose.ui.graphics.vector.ImageVector,
    isPassword    : Boolean = false,
    value         : String,
    onValueChange : (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value                = value,
            onValueChange        = onValueChange,
            placeholder          = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
            leadingIcon          = { Icon(icon, null, tint = TextMuted) },
            trailingIcon         = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            null, tint = TextMuted
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            singleLine           = true,
            modifier             = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            colors               = TextFieldDefaults.colors(
                focusedContainerColor    = FieldBg,
                unfocusedContainerColor  = FieldBg,
                focusedIndicatorColor    = Color.Transparent,
                unfocusedIndicatorColor  = Color.Transparent,
                focusedLeadingIconColor  = RedPrimary,
                unfocusedLeadingIconColor = TextMuted
            )
        )
    }
}

@Composable
private fun RestaurantButton(
    text    : String,
    color   : Color,
    enabled : Boolean,
    onClick : () -> Unit
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = Modifier.fillMaxWidth().height(54.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = color,
            contentColor           = Color.White,
            disabledContainerColor = color.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}