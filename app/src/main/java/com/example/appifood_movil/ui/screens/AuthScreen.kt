package com.example.appifood_movil.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val FieldBg      = Color(0xFFF7F7F7)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

private const val WEB_CLIENT_ID =
    "1059401661862-41e49139vjdnfalomt49i2bqv416gbo8.apps.googleusercontent.com"

enum class AuthState { LOGIN, REGISTER, FORGOT_PASSWORD, VERIFY_CODE }

// ──────────────────────────────────────────────────────────────────
// AuthScreen
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthScreen(
    onLoginNavigation: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var screenState by remember { mutableStateOf(AuthState.LOGIN) }
    val context   = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsState()
    val error     by authViewModel.error.collectAsState()
    val user      by authViewModel.user.collectAsState()

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }

    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "authFadeIn"
    )
    val headerOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else (-30).dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label         = "headerSlide"
    )
    val cardOffsetY by animateDpAsState(
        targetValue   = if (visible) 0.dp else 40.dp,
        animationSpec = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
        label         = "cardSlide"
    )

    LaunchedEffect(Unit) {
        authViewModel.initGoogleSignIn(context, WEB_CLIENT_ID)
        visible = true
    }

    // Navegación cuando el usuario se autentica
    LaunchedEffect(user) {
        if (user != null) onLoginNavigation()
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { authViewModel.signInWithGoogle(it) }
            } catch (e: ApiException) {
                android.util.Log.e("AuthScreen", "Google sign-in failed: ${e.statusCode}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(RedPrimary, RedDark, RedDeep))
            )
            .graphicsLayer { alpha = screenAlpha }
    ) {
        AuthDecorativeCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AuthHeader(
                modifier    = Modifier.offset(y = headerOffsetY),
                screenState = screenState
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardOffsetY)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp, bottom = 40.dp)
            ) {
                Column {
                    AuthTitleBlock(screenState = screenState, error = error)
                    Spacer(modifier = Modifier.height(24.dp))

                    when (screenState) {
                        AuthState.LOGIN -> LoginForm(
                            onLoginClick = { email, pass ->
                                authViewModel.signInWithEmail(
                                    email    = email,
                                    password = pass,
                                    onSuccess = {}
                                )
                            },
                            onRegisterSwitch = { screenState = AuthState.REGISTER },
                            onForgotClick    = { screenState = AuthState.FORGOT_PASSWORD },
                            onGoogleClick    = {
                                googleSignInLauncher.launch(
                                    authViewModel.getGoogleSignInIntent()
                                )
                            }
                        )
                        AuthState.REGISTER -> RegisterForm(
                            onLoginSwitch = { screenState = AuthState.LOGIN },
                            authViewModel = authViewModel  // ← PASAMOS EL VIEWMODEL
                        )
                        AuthState.FORGOT_PASSWORD -> ForgotPasswordForm(
                            onBackToLogin = { screenState = AuthState.VERIFY_CODE }
                        )
                        AuthState.VERIFY_CODE -> VerificationCodeForm(
                            onContinue = { screenState = AuthState.LOGIN },
                            onBack     = { screenState = AuthState.FORGOT_PASSWORD }
                        )
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color       = YellowAccent,
                    strokeWidth = 3.dp,
                    modifier    = Modifier.size(48.dp)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// RegisterForm COMPLETO
// ──────────────────────────────────────────────────────────────────
@Composable
fun RegisterForm(
    onLoginSwitch: () -> Unit,
    authViewModel: AuthViewModel
) {
    var names by remember { mutableStateOf("") }
    var lastNames by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val error by authViewModel.error.collectAsState()

    // Validaciones
    val isPasswordValid = password.length >= 6
    val doPasswordsMatch = password == confirmPassword
    val isFormValid = names.isNotBlank() &&
            lastNames.isNotBlank() &&
            phone.isNotBlank() &&
            email.isNotBlank() &&
            isPasswordValid &&
            doPasswordsMatch &&
            confirmPassword.isNotBlank()

    Column {
        // Nombres
        AuthTextField(
            label = "Nombres *",
            placeholder = "Ej: Juan Carlos",
            icon = Icons.Default.Person,
            value = names,
            onValueChange = { names = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apellidos
        AuthTextField(
            label = "Apellidos *",
            placeholder = "Ej: Pérez García",
            icon = Icons.Default.Person,
            value = lastNames,
            onValueChange = { lastNames = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Teléfono
        AuthTextField(
            label = "Teléfono *",
            placeholder = "Ej: 3001234567",
            icon = Icons.Default.Phone,
            value = phone,
            onValueChange = { phone = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        AuthTextField(
            label = "Correo electrónico *",
            placeholder = "tucorreo@email.com",
            icon = Icons.Default.Email,
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Contraseña
        AuthTextField(
            label = "Contraseña *",
            placeholder = "Mínimo 6 caracteres",
            icon = Icons.Default.Lock,
            isPassword = true,
            value = password,
            onValueChange = { password = it }
        )

        if (password.isNotBlank() && !isPasswordValid) {
            Text(
                text = "⚠️ La contraseña debe tener al menos 6 caracteres",
                color = RedPrimary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Confirmar Contraseña
        AuthTextField(
            label = "Confirmar contraseña *",
            placeholder = "Repite tu contraseña",
            icon = Icons.Default.Lock,
            isPassword = true,
            value = confirmPassword,
            onValueChange = { confirmPassword = it }
        )

        if (confirmPassword.isNotBlank() && !doPasswordsMatch) {
            Text(
                text = "⚠️ Las contraseñas no coinciden",
                color = RedPrimary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de registro
        AuthPrimaryButton(
            text = "Crear cuenta",
            enabled = isFormValid
        ) {
            authViewModel.registerWithEmail(
                names = names.trim(),
                lastNames = lastNames.trim(),
                phone = phone.trim(),
                email = email.trim(),
                password = password,
                onSuccess = {
                    // La navegación se maneja en AuthScreen via LaunchedEffect(user)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        AuthDivider()
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onLoginSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = TextMuted)) { append("¿Ya tienes cuenta? ") }
                withStyle(SpanStyle(color = RedPrimary, fontWeight = FontWeight.Bold)) {
                    append("Inicia sesión")
                }
            })
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// LoginForm
// ──────────────────────────────────────────────────────────────────
@Composable
fun LoginForm(
    onLoginClick     : (email: String, password: String) -> Unit,
    onRegisterSwitch : () -> Unit,
    onForgotClick    : () -> Unit,
    onGoogleClick    : () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column {
        AuthTextField(
            label         = "Correo electrónico",
            placeholder   = "tucorreo@email.com",
            icon          = Icons.Default.Email,
            value         = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            label         = "Contraseña",
            placeholder   = "••••••••",
            icon          = Icons.Default.Lock,
            isPassword    = true,
            value         = password,
            onValueChange = { password = it }
        )

        TextButton(
            onClick  = onForgotClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("¿Olvidaste tu contraseña?", color = RedPrimary, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        AuthPrimaryButton(
            text    = "Iniciar sesión",
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            onLoginClick(email.trim(), password)
        }

        Spacer(modifier = Modifier.height(16.dp))
        AuthDivider()
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick  = onGoogleClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape    = RoundedCornerShape(16.dp),
            border   = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE0E0E0))
        ) {
            Icon(
                painter            = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = null,
                modifier           = Modifier.size(20.dp),
                tint               = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Continuar con Google", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick  = onRegisterSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = TextMuted)) { append("¿No tienes cuenta? ") }
                withStyle(SpanStyle(color = RedPrimary, fontWeight = FontWeight.Bold)) {
                    append("Regístrate gratis")
                }
            })
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// AuthTextField
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthTextField(
    label         : String,
    placeholder   : String,
    icon          : ImageVector,
    isPassword    : Boolean = false,
    value         : String,
    onValueChange : (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value                = value,
            onValueChange        = onValueChange,
            placeholder          = { Text(placeholder, color = TextMuted, fontSize = 14.sp) },
            leadingIcon          = {
                Icon(imageVector = icon, contentDescription = null, tint = TextMuted)
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector        = if (isPasswordVisible)
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPasswordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña",
                            tint               = TextMuted
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !isPasswordVisible)
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

// ──────────────────────────────────────────────────────────────────
// AuthPrimaryButton
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthPrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = Modifier.fillMaxWidth().height(54.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = RedPrimary,
            contentColor           = Color.White,
            disabledContainerColor = Color(0xFFE57373),
            disabledContentColor   = Color.White.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ──────────────────────────────────────────────────────────────────
// AuthDivider
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthDivider() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        Text("  o continúa con  ", color = TextMuted, fontSize = 12.sp)
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
    }
}

// ──────────────────────────────────────────────────────────────────
// AuthHeader
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthHeader(modifier: Modifier = Modifier, screenState: AuthState) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(top = 56.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )
            Text("AppiFood", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        val tagText = when (screenState) {
            AuthState.LOGIN           -> "🍔 Bienvenido de vuelta"
            AuthState.REGISTER        -> "🎉 Únete a AppiFood"
            AuthState.FORGOT_PASSWORD -> "🔑 Recupera tu acceso"
            AuthState.VERIFY_CODE     -> "📩 Revisa tu correo"
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.18f)
        ) {
            Text(
                text       = tagText,
                color      = Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// AuthTitleBlock
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthTitleBlock(screenState: AuthState, error: String?) {
    val title = when (screenState) {
        AuthState.LOGIN           -> "¡Bienvenido de nuevo!"
        AuthState.REGISTER        -> "Crear cuenta gratis"
        AuthState.FORGOT_PASSWORD -> "Actualiza tu contraseña"
        AuthState.VERIFY_CODE     -> "Código de verificación"
    }

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(50))
            .background(YellowAccent)
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

    if (!error.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFEBEB)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Warning,
                    contentDescription = null,
                    tint               = RedPrimary,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = error, color = RedPrimary, fontSize = 13.sp)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// AuthDecorativeCircles
// ──────────────────────────────────────────────────────────────────
@Composable
fun AuthDecorativeCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
    }
}

// ──────────────────────────────────────────────────────────────────
// ForgotPasswordForm
// ──────────────────────────────────────────────────────────────────
@Composable
fun ForgotPasswordForm(
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Column {
        AuthTextField(
            label = "Correo electrónico",
            placeholder = "tucorreo@email.com",
            icon = Icons.Default.Email,
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthPrimaryButton(
            text = "Enviar código",
            enabled = email.isNotBlank()
        ) {
            onBackToLogin()
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Volver al inicio", color = RedPrimary)
        }
    }
}

// ──────────────────────────────────────────────────────────────────
// VerificationCodeForm
// ──────────────────────────────────────────────────────────────────
@Composable
fun VerificationCodeForm(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    Column {
        AuthTextField(
            label = "Código de verificación",
            placeholder = "Ingresa el código de 6 dígitos",
            icon = Icons.Default.Sms,
            value = code,
            onValueChange = { code = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthPrimaryButton(
            text = "Verificar",
            enabled = code.length == 6
        ) {
            onContinue()
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Volver", color = RedPrimary)
        }
    }
}