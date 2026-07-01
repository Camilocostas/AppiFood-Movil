package com.example.appifood_movil.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.ui.viewmodel.Resena
import com.example.appifood_movil.ui.viewmodel.ResenaViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionResenasScreen(
    navController: NavController,
    viewModel: ResenaViewModel = hiltViewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val restauranteId = user?.uid ?: ""
    val resenas by viewModel.resenas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(restauranteId) {
        if (restauranteId.isNotEmpty()) {
            viewModel.loadResenas(restauranteId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (resenas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⭐", fontSize = 60.sp)
                    Text(
                        text = "No hay reseñas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Todavía no tienes reseñas de clientes",
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(resenas) { resena ->
                    ResenaCard(
                        resena = resena,
                        onResponder = { respuesta ->
                            viewModel.responderResena(
                                restauranteId = restauranteId,
                                resenaId = resena.id,
                                respuesta = respuesta
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ResenaCard(
    resena: Resena,
    onResponder: (String) -> Unit
) {
    val context = LocalContext.current
    var showResponderDialog by remember { mutableStateOf(false) }

    // Formatear fecha
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = dateFormat.format(Date(resena.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Usuario y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1565C0).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = resena.usuario.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            fontSize = 18.sp
                        )
                    }

                    Column {
                        Text(
                            text = resena.usuario,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = fecha,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Calificación (estrellas)
                Row {
                    repeat(5) { index ->
                        Icon(
                            if (index < resena.calificacion) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < resena.calificacion) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comentario
            Text(
                text = resena.comentario,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )

            // Respuesta (si existe)
            if (resena.respuesta.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Respuesta del restaurante:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = resena.respuesta,
                            fontSize = 13.sp,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de responder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showResponderDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF1565C0)
                    )
                ) {
                    Icon(
                        if (resena.respuesta.isNotEmpty()) Icons.Default.Edit else Icons.Default.Reply,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (resena.respuesta.isNotEmpty()) "Editar respuesta" else "Responder"
                    )
                }
            }
        }
    }

    // Dialog para responder
    if (showResponderDialog) {
        ResponderDialog(
            resena = resena,
            onDismiss = { showResponderDialog = false },
            onResponder = { respuesta ->
                onResponder(respuesta)
                showResponderDialog = false
                Toast.makeText(context, "Respuesta enviada", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ResponderDialog(
    resena: Resena,
    onDismiss: () -> Unit,
    onResponder: (String) -> Unit
) {
    var respuesta by remember { mutableStateOf(resena.respuesta) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Responder a ${resena.usuario}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Calificación: ${resena.calificacion} ⭐",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = "\"${resena.comentario}\"",
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = respuesta,
                    onValueChange = { respuesta = it },
                    label = { Text("Tu respuesta") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (respuesta.isNotBlank()) {
                                onResponder(respuesta)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        enabled = respuesta.isNotBlank()
                    ) {
                        Text("Enviar respuesta")
                    }
                }
            }
        }
    }
}