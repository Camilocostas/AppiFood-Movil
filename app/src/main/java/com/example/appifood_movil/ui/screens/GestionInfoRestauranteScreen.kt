package com.example.appifood_movil.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appifood_movil.R
// ✅ IMPORTAR EL MODELO DEL VIEWMODEL, NO DEL DATA.MODEL
import com.example.appifood_movil.ui.viewmodel.RestaurantInfo
import com.example.appifood_movil.ui.viewmodel.RestaurantInfoViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionInfoRestauranteScreen(
    navController: NavController,
    viewModel: RestaurantInfoViewModel = hiltViewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val restauranteId = user?.uid ?: ""

    val info by viewModel.restaurantInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    // Cargar información
    LaunchedEffect(restauranteId) {
        if (restauranteId.isNotEmpty()) {
            viewModel.loadRestaurantInfo(restauranteId)
        }
    }

    // Launcher para portada
    val portadaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && restauranteId.isNotEmpty()) {
            viewModel.uploadPortadaImage(restauranteId, uri)
        }
    }

    // Launcher para galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && restauranteId.isNotEmpty()) {
            viewModel.uploadGaleriaImage(restauranteId, uri)
        }
    }
    // Después de llamar a uploadPortadaImage o uploadGaleriaImage
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    LaunchedEffect(uploadProgress) {
        when (uploadProgress) {
            100 -> Toast.makeText(context, "✅ Imagen subida", Toast.LENGTH_SHORT).show()
            -1 -> Toast.makeText(context, "❌ Error al subir imagen", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información del Restaurante") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, "Editar")
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Foto de portada
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            if (info?.imagenPortada?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = info?.imagenPortada,
                                    contentDescription = "Portada",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFE0E0E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }
                            FloatingActionButton(
                                onClick = { portadaLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .size(40.dp),
                                containerColor = Color(0xFF1565C0)
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Cambiar foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Información
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoRow("🏪", "Nombre", info?.nombre ?: "Sin nombre")
                            InfoRow("📝", "Descripción", info?.descripcion ?: "Sin descripción")
                            InfoRow("📂", "Categoría", info?.categoria ?: "Sin categoría")
                            InfoRow("📍", "Dirección", info?.direccion ?: "Sin dirección")
                            InfoRow("📞", "Teléfono", info?.telefono ?: "Sin teléfono")
                            InfoRow("🕐", "Horario", info?.horario ?: "Sin horario")
                        }
                    }
                }

                // Fotos de la galería
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Galería de fotos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                TextButton(
                                    onClick = { galeriaLauncher.launch("image/*") }
                                ) {
                                    Text("Agregar foto")
                                }
                            }

                            if (info?.fotosGaleria?.isNotEmpty() == true) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(info?.fotosGaleria ?: emptyList()) { fotoUrl ->
                                        Box {
                                            AsyncImage(
                                                model = fotoUrl,
                                                contentDescription = "Foto galería",
                                                modifier = Modifier
                                                    .size(100.dp, 80.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            IconButton(
                                                onClick = {
                                                    viewModel.removeGaleriaImage(restauranteId, fotoUrl)
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.White,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(Color.Red, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No hay fotos en la galería",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para editar información
    if (showEditDialog) {
        EditRestaurantInfoDialog(
            info = info,  // ✅ Ahora es del tipo correcto (RestaurantInfo del ViewModel)
            onDismiss = { showEditDialog = false },
            onSave = { nombre, descripcion, categoria, direccion, telefono, horario ->
                viewModel.updateRestaurantInfo(
                    restauranteId = restauranteId,
                    nombre = nombre,
                    descripcion = descripcion,
                    categoria = categoria,
                    direccion = direccion,
                    telefono = telefono,
                    horario = horario
                    // ✅ latitude y longitude se calculan automáticamente
                )
                showEditDialog = false
                Toast.makeText(context, "Información actualizada", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun InfoRow(icon: String, label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono con fondo
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1565C0).copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text     = label,
                    fontSize = 11.sp,
                    color    = Color(0xFF888888),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text       = value.ifEmpty { "No disponible" },
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            color     = Color(0xFFF0F0F0),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun EditRestaurantInfoDialog(
    info: RestaurantInfo?,  // ✅ Usa el modelo del ViewModel
    onDismiss: () -> Unit,
    onSave: (nombre: String, descripcion: String, categoria: String, direccion: String, telefono: String, horario: String) -> Unit
) {
    var nombre by remember { mutableStateOf(info?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(info?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(info?.categoria ?: "") }
    var direccion by remember { mutableStateOf(info?.direccion ?: "") }
    var telefono by remember { mutableStateOf(info?.telefono ?: "") }
    var horario by remember { mutableStateOf(info?.horario ?: "") }

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
                    text = "Editar Información",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del restaurante") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = horario,
                    onValueChange = { horario = it },
                    label = { Text("Horario (ej: Lun-Vie 8am-6pm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                            onSave(nombre, descripcion, categoria, direccion, telefono, horario)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}