package com.example.appifood_movil.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.components.ImageHeader
import com.example.appifood_movil.ui.theme.AppColors
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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
        authViewModel.initGoogleSignIn(context, webClientId)
        authViewModel.setOnAuthSuccess {
            if (!hasNavigated) {
                hasNavigated = true
                onLoginNavigation()
            }
        }
    }

    LaunchedEffect(user) {
        if (user != null && !hasNavigated) {
            hasNavigated = true
            onLoginNavigation()
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken // El error persistía aquí por metadatos
                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                android.util.Log.e("AuthScreen", "Google sign in failed", e)
            }
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

            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            
            if (error != null) {
                Text(text = error!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (screenState) {
                AuthState.LOGIN -> LoginForm(
                    onLoginClick = onLoginNavigation,
                    onRegisterSwitch = { screenState = AuthState.REGISTER },
                    onForgotClick = { screenState = AuthState.FORGOT_PASSWORD },
                    onGoogleClick = {
                        val signInIntent = authViewModel.getGoogleSignInIntent()
                        googleSignInLauncher.launch(signInIntent)
                    }
                )
                AuthState.REGISTER -> RegisterForm(
                    onLoginSwitch = { screenState = AuthState.LOGIN }
                )
                AuthState.FORGOT_PASSWORD -> ForgotPasswordForm(
                    onBackToLogin = { screenState = AuthState.VERIFY_CODE }
                )
                AuthState.VERIFY_CODE -> VerificationCodeForm(
                    onContinue = { screenState = AuthState.LOGIN },
                    onBack = { screenState = AuthState.FORGOT_PASSWORD }
                )
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppColors.AppiFoodRed)
        }
    }
}

@Composable
fun LoginForm(
    onLoginClick: () -> Unit,
    onRegisterSwitch: () -> Unit,
    onForgotClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        Spacer(modifier = Modifier.height(24.dp))

        MainActionButton(text = "Iniciar sesión") {
            // Lógica de login normal
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Google
        OutlinedButton(
            onClick = onGoogleClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Continuar con Google", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onRegisterSwitch, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = buildAnnotatedString {
                append("¿No tienes cuenta? ")
                withStyle(style = SpanStyle(color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold)) {
                    append("Regístrate gratis")
                }
            }, color = Color.Gray)
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
        Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = { Icon(icon, null, tint = Color.Gray) },
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
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
fun MainActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A))
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// Stubs para evitar errores de compilación si no están definidos
@Composable fun RegisterForm(onLoginSwitch: () -> Unit) {}
@Composable fun ForgotPasswordForm(onBackToLogin: () -> Unit) {}
@Composable fun VerificationCodeForm(onContinue: () -> Unit, onBack: () -> Unit) {}
