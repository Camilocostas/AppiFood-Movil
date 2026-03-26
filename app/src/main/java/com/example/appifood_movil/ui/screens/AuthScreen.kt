package com.example.appifood_movil.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.appifood_movil.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.ui.tooling.preview.Preview
import com.example.appifood_movil.ui.theme.AppifoodMovilTheme


@Composable
fun AuthScreen(onLoginNavigation: () -> Unit) {

    var isLogin by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showForm = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 🔥 Imagen superior
        Image(
            painter = painterResource(id = R.drawable.burger_background_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        )

        // 🔥 Curva blanca inferior
        AnimatedVisibility(
            visible = showForm,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(600)
            ) + fadeIn()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 280.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
            ) {

                Spacer(modifier = Modifier.height(60.dp))

                // 🔥 Toggle Login/Register
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                ) {

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isLogin) Color(0xFFFF4B3A) else Color.Transparent)
                            .clickable { isLogin = true }
                            .padding(horizontal = 32.dp, vertical = 10.dp)
                    ) {
                        Text(
                            "Log in",
                            color = if (isLogin) Color.White else Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (!isLogin) Color(0xFFFF4B3A) else Color.Transparent)
                            .clickable { isLogin = false }
                            .padding(horizontal = 32.dp, vertical = 10.dp)
                    ) {
                        Text(
                            "Register",
                            color = if (!isLogin) Color.White else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dentro de AuthScreen, busca el bloque del if(isLogin)
                if (isLogin) {
                    LoginForm(onLoginClick = onLoginNavigation) // Pasamos la función aquí
                } else {
                    RegisterForm()
                }
            }
        }

        // 🔥 Logo circular
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 180.dp)
                .size(130.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_appifood_2),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )
        }
    }
}

@Composable
fun LoginForm(onLoginClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {

        Text(
            text = "Welcome to AppiFood",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginClick() }, // <--- CAMBIO AQUÍ: Ahora ejecuta la navegación
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4B3A)
            )
        ) {
            Text("Log in")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Forgot Password?",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun RegisterForm() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome to AppiFood",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Mobile number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4B3A)
            )
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Already have account? Sign in",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    AppifoodMovilTheme {
        AuthScreen(onLoginNavigation = {})
    }
}
