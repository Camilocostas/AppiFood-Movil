// ui/screens/WriteReviewScreen.kt
package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.data.model.Review
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.appifood_movil.ui.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    navController: NavController,
    restaurantUid: String,
    restaurantName: String,
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escribir reseña", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Califica tu experiencia en",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = restaurantName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Selector de estrellas
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val starNumber = index + 1
                    IconButton(
                        onClick = { rating = starNumber },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (starNumber <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "$starNumber estrellas",
                            tint = if (starNumber <= rating) Color(0xFFFFD600) else Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Text(
                text = when (rating) {
                    0 -> "Selecciona una calificación"
                    1 -> "⭐ Muy malo"
                    2 -> "⭐⭐ Malo"
                    3 -> "⭐⭐⭐ Regular"
                    4 -> "⭐⭐⭐⭐ Bueno"
                    5 -> "⭐⭐⭐⭐⭐ Excelente"
                    else -> ""
                },
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de comentario
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Tu opinión (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 4,
                maxLines = 8
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón enviar
            Button(
                onClick = {
                    if (rating == 0) {
                        // Mostrar error
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        val review = Review(
                            restaurantUid = restaurantUid,
                            restaurantName = restaurantName,
                            userId = currentUser?.uid ?: "",
                            userName = currentUser?.displayName ?: "Usuario",
                            userPhoto = currentUser?.photoUrl?.toString() ?: "",
                            rating = rating,
                            comment = comment,
                            createdAt = System.currentTimeMillis()
                        )
                        val success = reviewViewModel.saveReview(review)
                        isLoading = false
                        if (success) {
                            showSuccess = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                enabled = !isLoading && rating > 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar reseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (showSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1D9E75).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, tint = Color(0xFF1D9E75), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("¡Reseña enviada!", color = Color(0xFF1D9E75), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F).copy(alpha = 0.8f))
                ) {
                    Text("Volver")
                }
            }
        }
    }
}