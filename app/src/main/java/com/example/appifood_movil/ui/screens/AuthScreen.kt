package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.appifood_movil.R

private val AppiFoodRed = Color(0xFFFF4B3A)

@Composable
fun AuthScreen(onLoginNavigation: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { showForm = true }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.burger_background_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(350.dp)
        )

        AnimatedVisibility(
            visible = showForm,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(600)) + fadeIn()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 280.dp)
                    .background(Color.White, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                ) {
                    AuthTabItem("Log in", isLogin) { isLogin = true }
                    AuthTabItem("Register", !isLogin) { isLogin = false }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLogin) {
                    LoginForm(onLoginClick = onLoginNavigation)
                } else {
                    RegisterForm()
                }
            }
        }

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
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
        WelcomeHeader()
        AuthTextField(label = "Username")
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(label = "Password", isPassword = true)
        Spacer(modifier = Modifier.height(24.dp))
        AuthButton("Log in") { onLoginClick() }
        Text("Forgot Password?", modifier = Modifier.padding(top = 12.dp).align(Alignment.CenterHorizontally))
    }
}

@Composable
fun RegisterForm() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
        WelcomeHeader()
        AuthTextField(label = "Username")
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(label = "Mobile number")
        Spacer(modifier = Modifier.height(12.dp))
        AuthTextField(label = "Password", isPassword = true)
        Spacer(modifier = Modifier.height(24.dp))
        AuthButton("Register") { }
        Text("Already have account? Sign in", modifier = Modifier.padding(top = 12.dp).align(Alignment.CenterHorizontally))
    }
}

@Composable
fun WelcomeHeader() {
    Text(
        text = "Welcome to AppiFood",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )
}

@Composable
fun AuthTabItem(text: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isActive) AppiFoodRed else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 32.dp, vertical = 10.dp)
    ) {
        Text(text, color = if (isActive) Color.White else Color.Gray)
    }
}

@Composable
fun AuthTextField(label: String, isPassword: Boolean = false) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun AuthButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppiFoodRed,
            contentColor = Color.White)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
