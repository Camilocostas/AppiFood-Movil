package com.example.appifood_movil.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.components.ImageHeader
import com.example.appifood_movil.ui.theme.AppColors
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

enum class AuthState { LOGIN, REGISTER, FORGOT_PASSWORD, VERIFY_CODE }

@Composable
fun AuthScreen(
    onLoginNavigation: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var screenState by remember { mutableStateOf(AuthState.LOGIN) }
    val context = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val user by authViewModel.user.collectAsState()

    var hasNavigated by remember { mutableStateOf(false) }

    val webClientId = "1059401661862-41e49139vjdnfalomt49i2bqv416gbo8.apps.googleusercontent.com"

    LaunchedEffect(Unit) {
        android.util.Log.d("AuthScreen", "📱 Inicializando AuthViewModel")
        authViewModel.initGoogleSignIn(context, webClientId)

        authViewModel.setOnAuthSuccess {
            android.util.Log.d("AuthScreen", "🚀 CALLBACK EJECUTADO - NAVEGANDO A HOME")
            if (!hasNavigated) {
                hasNavigated = true
                onLoginNavigation()
            }
        }
    }

    LaunchedEffect(user) {
        android.util.Log.d("AuthScreen", "👤 user cambió: $user")
        if (user != null && !hasNavigated) {
            android.util.Log.d("AuthScreen", "🚀 NAVEGANDO DESDE LAUNCHEDEFFECT")
            hasNavigated = true
            onLoginNavigation()
        }
    }

    SideEffect {
        if (user != null && !hasNavigated) {
            android.util.Log.d("AuthScreen", "🚀 NAVEGANDO DESDE SIDEEFFECT")
            hasNavigated = true
            onLoginNavigation()
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("AuthScreen", "📱 ResultCode: ${result.resultCode}")

        if (result.resultCode == Activity.RESULT_OK) {
            val data: android.content.Intent? = result.data
            try {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account: GoogleSignInAccount? = task.result
                val idToken: String? = account?.idToken

                android.util.Log.d("AuthScreen", "🔑 idToken: ${idToken?.take(20)}...")
                android.util.Log.d("AuthScreen", "👤 account email: ${account?.email}")

                if (idToken != null) {
                    android.util.Log.d("AuthScreen", "✅ Token obtenido, llamando a signInWithGoogle")
                    authViewModel.signInWithGoogle(idToken)
                } else {
                    android.util.Log.e("AuthScreen", "❌ idToken es null")
                    authViewModel.setError("No se pudo obtener el token de Google")
                }
            } catch (e: ApiException) {
                android.util.Log.e("AuthScreen", "❌ ApiException: ${e.message}")
                authViewModel.setError("Error al iniciar sesión con Google: ${e.message}")
            } catch (e: Exception) {
                android.util.Log.e("AuthScreen", "❌ Exception: ${e.message}")
                authViewModel.setError("Error inesperado: ${e.message}")
            }
        } else {
            android.util.Log.e("AuthScreen", "❌ ResultCode no es OK: ${result.resultCode}")
            authViewModel.setError("Inicio de sesión cancelado")
        }
    }

    Column(
        modifier = Modifier
            .background(AppColors.AppiFoodRed)
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        ImageHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(24.dp)
        ) {
            val title = when (screenState) {
                AuthState.LOGIN -> "¡Bienvenido de nuevo!"
                AuthState.REGISTER -> "Crear cuenta gratis"
                AuthState.FORGOT_PASSWORD -> "Actualiza tu contraseña"
                AuthState.VERIFY_CODE -> "Código de verificación"
            }

            val subtitle = when (screenState) {
                AuthState.LOGIN -> "Inicia sesión para continuar"
                AuthState.REGISTER -> "Únete a AppiFood hoy"
                AuthState.FORGOT_PASSWORD -> "Crea una nueva contraseña para tu cuenta."
                AuthState.VERIFY_CODE -> "Por favor ingresa el código que acabamos de enviar al correo"
            }

            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = subtitle, fontSize = 15.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // ⭐ WHEN - CORRECTAMENTE CERRADO
            when (screenState) {
                AuthState.LOGIN -> LoginForm(
                    onLoginClick = { email, password ->
                        android.util.Log.d("AuthScreen", "📧 Iniciando sesión con email: $email")
                        authViewModel.signInWithEmail(email, password) {
                            android.util.Log.d("AuthScreen", "🚀 NAVEGANDO DESDE EMAIL")
                            if (!hasNavigated) {
                                hasNavigated = true
                                onLoginNavigation()
                            }
                        }
                    },
                    onRegisterSwitch = { screenState = AuthState.REGISTER },
                    onForgotClick = { screenState = AuthState.FORGOT_PASSWORD },
                    onGoogleSignInClick = {
                        android.util.Log.d("AuthScreen", "🔄 Iniciando Google Sign-In")
                        hasNavigated = false
                        val signInIntent = GoogleSignIn.getClient(
                            context,
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(webClientId)
                                .requestEmail()
                                .build()
                        ).signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    isLoading = isLoading
                )

                AuthState.REGISTER -> RegisterForm(
                    onLoginSwitch = { screenState = AuthState.LOGIN },
                    onRegisterSuccess = {
                        android.util.Log.d("AuthScreen", "🚀 NAVEGANDO A HOME DESDE REGISTRO")
                        if (!hasNavigated) {
                            hasNavigated = true
                            onLoginNavigation()
                        }
                    },
                    authViewModel = authViewModel
                )

                AuthState.FORGOT_PASSWORD -> ForgotPasswordForm(
                    onBackToLogin = { screenState = AuthState.VERIFY_CODE }
                )

                AuthState.VERIFY_CODE -> VerificationCodeForm(
                    onContinue = { screenState = AuthState.LOGIN },
                    onBack = { screenState = AuthState.FORGOT_PASSWORD }
                )
            } // ⭐ AQUÍ TERMINA EL WHEN
        }
    }
}

// ⭐ EL RESTO DE TUS FUNCIONES VAN FUERA DEL WHEN

@Composable
fun VerificationCodeForm(onContinue: () -> Unit, onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = buildAnnotatedString {
                append("Reenviar código en ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("00:15")
                }
            },
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        MainActionButton(text = "Continuar") { onContinue() }

        TextButton(onClick = onBack) {
            Text("Volver", color = Color.Gray)
        }
    }
}

@Composable
fun RegisterForm(
    onLoginSwitch: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val appiFoodRed = Color(0xFFFF4B3A)
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()

    Column {
        error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        CustomTextField(
            label = "Correo electrónico",
            placeholder = "tucorreo@email.com",
            icon = Icons.Default.Email,
            text = email,
            onTextChange = { email = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Contraseña",
            placeholder = "••••••••",
            icon = Icons.Default.Lock,
            isPassword = true,
            text = password,
            onTextChange = { password = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (password.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            when {
                                password.length < 6 -> Color.Red
                                password.length < 10 -> Color(0xFFFF9800)
                                else -> Color.Green
                            }.copy(alpha = 0.4f)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        password.length < 6 -> "débil"
                        password.length < 10 -> "media"
                        else -> "fuerte"
                    },
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Confirmar contraseña",
            placeholder = "Repite tu contraseña",
            icon = Icons.Default.Lock,
            isPassword = true,
            text = confirmPassword,
            onTextChange = { confirmPassword = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        MainActionButton(
            text = "Crear cuenta",
            icon = Icons.Default.PersonAdd,
            isLoading = isLoading
        ) {
            when {
                email.isEmpty() -> {
                    authViewModel.setError("El correo electrónico es requerido")
                }
                password.isEmpty() -> {
                    authViewModel.setError("La contraseña es requerida")
                }
                password.length < 6 -> {
                    authViewModel.setError("La contraseña debe tener al menos 6 caracteres")
                }
                password != confirmPassword -> {
                    authViewModel.setError("Las contraseñas no coinciden")
                }
                else -> {
                    authViewModel.createUserWithEmail(email, password) {
                        onRegisterSuccess()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = onLoginSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("¿Ya tienes cuenta? ")
                    withStyle(style = SpanStyle(color = appiFoodRed, fontWeight = FontWeight.Bold)) {
                        append("Inicia sesión")
                    }
                },
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LoginForm(
    onLoginClick: (String, String) -> Unit,
    onRegisterSwitch: () -> Unit,
    onForgotClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    isLoading: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val appiFoodRed = Color(0xFFFF4B3A)

    Column {
        CustomTextField(
            label = "Correo electrónico",
            placeholder = "tucorreo@email.com",
            icon = Icons.Default.Email,
            text = email,
            onTextChange = { email = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Contraseña",
            placeholder = "••••••••",
            icon = Icons.Default.Lock,
            isPassword = true,
            text = password,
            onTextChange = { password = it }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var checked by remember { mutableStateOf(false) }
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.scale(0.8f),
                    colors = CheckboxDefaults.colors(checkedColor = appiFoodRed)
                )
                Text(
                    "Recordarme",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                "¿Olvidaste tu contraseña?",
                color = appiFoodRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onForgotClick() }
            )
        }

        GoogleSignInButton(
            onClick = onGoogleSignInClick,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color.LightGray,
                thickness = 1.dp
            )
            Text(
                text = "o",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color.LightGray,
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        MainActionButton(
            text = "Iniciar sesión",
            icon = Icons.Default.ArrowForward,
            isLoading = isLoading
        ) {
            onLoginClick(email, password)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = onRegisterSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("¿No tienes cuenta? ")
                    withStyle(style = SpanStyle(color = appiFoodRed, fontWeight = FontWeight.Bold)) {
                        append("Regístrate gratis")
                    }
                },
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFFFF4B3A)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Iniciar sesión con Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun MainActionButton(
    text: String,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    val appiFoodRed = Color(0xFFFF4B3A)

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = appiFoodRed,
            disabledContainerColor = appiFoodRed.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                if (icon != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    text: String,
    onTextChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
            visualTransformation = if (isPassword && !isPasswordVisible)
                PasswordVisualTransformation()
            else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = if (isPasswordVisible) "Ocultar" else "Ver",
                            modifier = Modifier.size(18.dp),
                            tint = Color.Gray.copy(alpha = 0.6f)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ForgotPasswordForm(onBackToLogin: () -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Column {
        CustomTextField(
            label = "Nueva contraseña",
            placeholder = "mínimo 8 caracteres",
            icon = Icons.Default.Lock,
            isPassword = true,
            text = newPassword,
            onTextChange = { newPassword = it }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFFF4B3A).copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("débil", color = Color(0xFFFF4B3A).copy(alpha = 0.5f), fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Confirmar contraseña",
            placeholder = "repite la contraseña",
            icon = Icons.Default.Lock,
            isPassword = true,
            text = confirmPassword,
            onTextChange = { confirmPassword = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Instrucciones",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        val instructions = listOf(
            "Usa diferentes tipos de caracteres",
            "Letras mayúsculas (A-Z)",
            "Letras minúsculas (a-z)",
            "Números (0-9)",
            "Símbolos especiales (!, @, #, $, %)"
        )

        instructions.forEach { text ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text("• ", color = Color.Gray)
                Text(text, fontSize = 13.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        MainActionButton(text = "Guardar nueva contraseña") {
            onBackToLogin()
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Volver al inicio de sesión", color = Color.Gray, fontSize = 14.sp)
        }
    }
}