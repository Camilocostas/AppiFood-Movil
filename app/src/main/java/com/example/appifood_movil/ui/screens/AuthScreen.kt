package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.appifood_movil.R
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.PersonAdd
import com.example.appifood_movil.ui.components.ImageHeader
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.appifood_movil.ui.theme.AppColors

enum class AuthState { LOGIN, REGISTER, FORGOT_PASSWORD, VERIFY_CODE }

@Composable
fun AuthScreen(onLoginNavigation: () -> Unit) {
    var screenState by remember { mutableStateOf(AuthState.LOGIN) }

    Column(
        modifier = Modifier.background(AppColors.AppiFoodRed)
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
            val title = when(screenState) {
                AuthState.LOGIN -> "¡Bienvenido de nuevo!"
                AuthState.REGISTER -> "Crear cuenta gratis"
                AuthState.FORGOT_PASSWORD -> "Actualiza tu contraseña"
                AuthState.VERIFY_CODE -> "Código de verificación"
            }

            val subtitle = when(screenState) {
                AuthState.LOGIN -> "Inicia sesión para continuar"
                AuthState.REGISTER -> "Únete a AppiFood hoy"
                AuthState.FORGOT_PASSWORD -> "Crea una nueva contraseña para tu cuenta."
                AuthState.VERIFY_CODE -> "Por favor ingresa el código que acabamos de enviar al correo mauricio@ejemplo.com"
            }

            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = subtitle, fontSize = 15.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

            when (screenState) {
                AuthState.LOGIN -> LoginForm(
                    onLoginClick = onLoginNavigation,
                    onRegisterSwitch = { screenState = AuthState.REGISTER },
                    onForgotClick = { screenState = AuthState.FORGOT_PASSWORD }
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
}

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
fun RegisterForm(onLoginSwitch: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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

        MainActionButton(text = "Crear cuenta", icon = Icons.Default.PersonAdd) {
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = onLoginSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("¿Ya tienes cuenta? ")
                    withStyle(style = SpanStyle(color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold)) {
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
fun LoginForm(onLoginClick: () -> Unit, onRegisterSwitch: () -> Unit, onForgotClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

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
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF4B3A))
                )
                Text(
                    "Recordarme",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                "¿Olvidaste tu contraseña?",
                color = Color(0xFFFF4B3A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onForgotClick() }
            )
        }

        MainActionButton(
            text = "Iniciar sesión",
            icon = Icons.Default.ArrowForward
        ) {
            if (email == "m" && password == "1") {
                onLoginClick()
            } else {
                errorMessage = "Correo o contraseña incorrectos"
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = onRegisterSwitch,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("¿No tienes cuenta? ")
                    withStyle(style = SpanStyle(color = Color(0xFFFF4B3A), fontWeight = FontWeight.Bold)) {
                        append("Regístrate gratis")
                    }
                },
                fontSize = 14.sp,
                color = Color.Gray
            )
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
fun MainActionButton(text: String, icon: ImageVector? = null, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF4B3A)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            if (icon != null) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(icon, null, modifier = Modifier.size(20.dp))
            }
        }
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
