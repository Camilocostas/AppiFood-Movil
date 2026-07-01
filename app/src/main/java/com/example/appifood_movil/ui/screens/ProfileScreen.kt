package com.example.appifood_movil.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.UserData
import com.example.appifood_movil.data.model.MockFirebaseUser
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Paleta unificada ──────────────────────────────────────────────
private val RedPrimary   = Color(0xFFD32F2F)
private val RedDark      = Color(0xFFB71C1C)
private val RedDeep      = Color(0xFF7F0000)
private val YellowAccent = Color(0xFFFFD600)
private val FieldBg      = Color(0xFFF7F7F7)
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF888888)

@Composable
fun ProfileScreen(
    navController : NavController,
    authViewModel : AuthViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val user: MockFirebaseUser? by authViewModel.user.collectAsState(initial = null)
    val userData by authViewModel.userData.collectAsState(initial = null)
    val isLoading by authViewModel.isLoading.collectAsState(initial = false)

    // ── Estado de edición ──────────────────────────────────────────
    var isEditing by remember { mutableStateOf(false) }
    var editedNames by remember { mutableStateOf("") }
    var editedLastNames by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }

    var showSaveAnimation by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }

    // ✅ Dialog para opciones de foto
    var showImageDialog by remember { mutableStateOf(false) }

    // ── Cargar datos cuando el usuario esté disponible ────────────
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            authViewModel.getUserDataFromFirestore(uid)
        }
    }

    // ── Actualizar campos editables cuando cambie userData ────────
    LaunchedEffect(userData) {
        userData?.let { data ->
            editedNames = data.names
            editedLastNames = data.lastNames
            editedPhone = data.phone
        }
    }

    // ── Launcher para seleccionar imagen ──────────────────────────
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploadingImage = true
                val uid = user?.uid
                if (uid != null) {
                    Log.d("ProfileScreen", "📤 Subiendo imagen...")
                    val result = authViewModel.uploadProfileImage(uid, it)
                    if (result != null) {
                        Log.d("ProfileScreen", "✅ Imagen subida: $result")
                        authViewModel.getUserDataFromFirestore(uid)
                    } else {
                        Log.e("ProfileScreen", "❌ Error al subir imagen")
                    }
                }
                isUploadingImage = false
            }
        }
    }

    // ── Animación de entrada ──────────────────────────────────────
    var visible by remember { mutableStateOf(false) }

    val screenAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label         = "profileFadeIn"
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

    LaunchedEffect(Unit) { visible = true }

    val initial = userData?.names?.firstOrNull()?.toString()?.uppercase() ?: "U"
    val imageUrl = userData?.imageUrl ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(RedPrimary, RedDark, RedDeep)))
            .graphicsLayer { alpha = screenAlpha }
    ) {
        ProfileDecorativeCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                modifier  = Modifier.offset(y = headerOffsetY),
                initial   = initial,
                names     = if (isEditing) editedNames else userData?.names ?: "",
                lastNames = if (isEditing) editedLastNames else userData?.lastNames ?: "",
                email     = userData?.email ?: user?.email ?: "",
                imageUrl  = imageUrl,
                isLoading = isLoading,
                onImageClick = {
                    if (imageUrl.isNotEmpty()) {
                        showImageDialog = true  // ✅ Mostrar diálogo
                    } else {
                        imagePickerLauncher.launch("image/*")
                    }
                }
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
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(YellowAccent)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = "Información de tu cuenta",
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = TextPrimary
                        )

                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    userData?.let { data ->
                                        editedNames = data.names
                                        editedLastNames = data.lastNames
                                        editedPhone = data.phone
                                    }
                                    isEditing = false
                                } else {
                                    isEditing = true
                                }
                            }
                        ) {
                            Icon(
                                if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Cancelar edición" else "Editar",
                                tint = RedPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileInfoCardEditable(
                        userData = userData,
                        isLoading = isLoading,
                        isEditing = isEditing,
                        editedNames = editedNames,
                        editedLastNames = editedLastNames,
                        editedPhone = editedPhone,
                        onNamesChange = { editedNames = it },
                        onLastNamesChange = { editedLastNames = it },
                        onPhoneChange = { editedPhone = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileOptionsCard(navController = navController)

                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileSaveButton(
                        isEditing = isEditing,
                        onClick = {
                            if (isEditing) {
                                scope.launch {
                                    val uid = user?.uid
                                    if (uid != null) {
                                        val updatedData = UserData(
                                            names = editedNames.trim(),
                                            lastNames = editedLastNames.trim(),
                                            email = userData?.email ?: "",
                                            phone = editedPhone.trim(),
                                            imageUrl = userData?.imageUrl ?: "",
                                            address = userData?.address ?: "",
                                            createdAt = userData?.createdAt ?: System.currentTimeMillis()
                                        )

                                        authViewModel.updateUserDataInFirestore(
                                            uid = uid,
                                            userData = updatedData,
                                            onComplete = { success ->
                                                if (success) {
                                                    scope.launch {
                                                        showSaveAnimation = true
                                                        delay(1500)
                                                        showSaveAnimation = false
                                                        isEditing = false
                                                        authViewModel.getUserDataFromFirestore(uid)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileLogoutButton(
                        isLoading = isLoggingOut,
                        onClick   = {
                            scope.launch {
                                isLoggingOut = true
                                delay(800)
                                authViewModel.signOut()
                                navController.navigate(Screen.RoleSelection.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                                isLoggingOut = false
                            }
                        }
                    )
                }
            }
        }

        // ── Overlay "Cambios guardados" ───────────────────────────
        AnimatedVisibility(
            visible      = showSaveAnimation,
            enter        = fadeIn() + scaleIn(initialScale = 0.8f),
            exit         = fadeOut() + scaleOut(targetScale = 1.2f)
        ) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier        = Modifier.size(220.dp, 180.dp),
                    shape           = RoundedCornerShape(28.dp),
                    color           = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier            = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(RedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null,
                                tint     = Color.White,
                                modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("¡Cambios guardados!",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 17.sp,
                            color      = Color.Black)
                    }
                }
            }
        }

        // ── Loading overlay ───────────────────────────────────────
        if (isLoading || isUploadingImage) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color       = YellowAccent,
                        strokeWidth = 3.dp,
                        modifier    = Modifier.size(48.dp)
                    )
                    if (isUploadingImage) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Subiendo imagen...",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // ── Diálogo para opciones de foto ─────────────────────────────
    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Foto de perfil") },
            text = { Text("¿Qué deseas hacer con tu foto de perfil?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageDialog = false
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Text("Cambiar foto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            showImageDialog = false
                            val uid = user?.uid
                            if (uid != null) {
                                authViewModel.deleteProfileImage(uid)
                                authViewModel.getUserDataFromFirestore(uid)
                            }
                        }
                    }
                ) {
                    Text("Eliminar foto", color = Color.Red)
                }
            }
        )
    }
}

// ── Círculos decorativos ──────────────────────────────────────────
@Composable
fun ProfileDecorativeCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.size(260.dp).offset(x = (-80).dp, y = (-60).dp)
            .clip(CircleShape).background(Color.White.copy(alpha = 0.05f)))
        Box(modifier = Modifier.size(180.dp).align(Alignment.TopEnd)
            .offset(x = 60.dp, y = 40.dp)
            .clip(CircleShape).background(Color.White.copy(alpha = 0.04f)))
        Box(modifier = Modifier.size(150.dp).align(Alignment.BottomStart)
            .offset(x = (-40).dp, y = 60.dp)
            .clip(CircleShape).background(Color.White.copy(alpha = 0.03f)))
    }
}

// ── Header del perfil ─────────────────────────────────────────────
@Composable
fun ProfileHeader(
    modifier  : Modifier = Modifier,
    initial   : String,
    names     : String,
    lastNames : String,
    email     : String,
    imageUrl  : String,
    isLoading : Boolean,
    onImageClick: () -> Unit
) {
    Column(
        modifier            = modifier.fillMaxWidth().padding(top = 56.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clickable { onImageClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_launcher_foreground)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8A80)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Text(
                            initial,
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp),
                shape = CircleShape,
                color = RedPrimary,
                contentColor = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (imageUrl.isNotEmpty()) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = if (imageUrl.isNotEmpty()) "Cambiar foto" else "Agregar foto",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.18f)) {
            Text("👤 Mi Perfil", color = Color.White, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp))
        }

        if (!isLoading) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text       = if (names.isNotBlank() || lastNames.isNotBlank())
                    "$names $lastNames" else "Usuario AppiFood",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(email, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        }
    }
}


// ── Tarjeta de información EDITABLE ──────────────────────────────
@Composable
fun ProfileInfoCardEditable(
    userData: UserData?,
    isLoading: Boolean,
    isEditing: Boolean,
    editedNames: String,
    editedLastNames: String,
    editedPhone: String,
    onNamesChange: (String) -> Unit,
    onLastNamesChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = FieldBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isLoading) {
                repeat(4) { i ->
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.width(80.dp).height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.2f)))
                        Box(modifier = Modifier.width(120.dp).height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.2f)))
                    }
                    if (i < 3) HorizontalDivider(color = Color.White, thickness = 1.dp)
                }
            } else {
                ProfileInfoRowEditable(
                    label = "NOMBRE(S)",
                    value = userData?.names ?: "No registrado",
                    isEditing = isEditing,
                    editedValue = editedNames,
                    onValueChange = onNamesChange
                )

                ProfileInfoRowEditable(
                    label = "APELLIDO(S)",
                    value = userData?.lastNames ?: "No registrado",
                    isEditing = isEditing,
                    editedValue = editedLastNames,
                    onValueChange = onLastNamesChange
                )

                ProfileInfoRow(
                    label = "CORREO ELECTRÓNICO",
                    value = userData?.email ?: "No registrado",
                    isLast = false
                )

                ProfileInfoRowEditable(
                    label = "TELÉFONO",
                    value = userData?.phone ?: "No registrado",
                    isEditing = isEditing,
                    editedValue = editedPhone,
                    onValueChange = onPhoneChange,
                    isLast = true
                )
            }
        }
    }
}

// ── Fila de información EDITABLE ──────────────────────────────────
@Composable
fun ProfileInfoRowEditable(
    label: String,
    value: String,
    isEditing: Boolean,
    editedValue: String,
    onValueChange: (String) -> Unit,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(0.4f)
            )

            if (isEditing) {
                TextField(
                    value = editedValue,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(0.6f)
                        .height(48.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = RedPrimary,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Text(
                    value.ifEmpty { "No registrado" },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(0.6f)
                )
            }
        }
        if (!isLast) HorizontalDivider(color = Color(0xFFE8E8E8), thickness = 1.dp)
    }
}

// ── Fila de información (NO editable) ────────────────────────────
@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(value.ifEmpty { "No registrado" }, fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp, color = TextPrimary)
        }
        if (!isLast) HorizontalDivider(color = Color(0xFFE8E8E8), thickness = 1.dp)
    }
}

// ── Tarjeta de opciones ───────────────────────────────────────────
@Composable
fun ProfileOptionsCard(navController: NavController) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = FieldBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
            ProfileMenuRow(Icons.Default.Person, "Mi Suscripción",
                onClick = { /* navegar a suscripción */ })
            ProfileMenuRow(Icons.Default.Payment, "Pagos",
                onClick = { navController.navigate(Screen.Payments.route) })
            ProfileMenuRow(Icons.Default.Notifications, "Centro de notificaciones",
                onClick = { /* navegar a notificaciones */ }, isLast = true)
        }
    }
}

// ── Fila de menú ──────────────────────────────────────────────────
@Composable
fun ProfileMenuRow(
    icon    : ImageVector,
    title   : String,
    onClick : () -> Unit = {},
    isLast  : Boolean   = false
) {
    Column {
        Row(
            modifier          = Modifier.fillMaxWidth().clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = RedPrimary,
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                    .background(RedPrimary.copy(alpha = 0.1f)).padding(6.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(title, modifier = Modifier.weight(1f), fontSize = 14.sp,
                fontWeight = FontWeight.Medium, color = TextPrimary)
            Icon(Icons.Default.ChevronRight, null, tint = TextMuted,
                modifier = Modifier.size(20.dp))
        }
        if (!isLast) HorizontalDivider(color = Color(0xFFE8E8E8), thickness = 1.dp)
    }
}

// ── Botón Guardar Cambios ─────────────────────────────────────────
@Composable
fun ProfileSaveButton(
    isEditing: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick   = onClick,
        enabled   = isEditing,
        modifier  = Modifier.fillMaxWidth().height(54.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor = RedPrimary,
            contentColor = Color.White,
            disabledContainerColor = RedPrimary.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(if (isEditing) "Guardar Cambios" else "Edita para guardar",
            fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Botón Cerrar Sesión ───────────────────────────────────────────
@Composable
fun ProfileLogoutButton(onClick: () -> Unit, isLoading: Boolean) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = !isLoading,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape    = RoundedCornerShape(16.dp),
        border   = BorderStroke(2.dp, RedPrimary),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = RedPrimary,
                strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        } else {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null,
                modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
