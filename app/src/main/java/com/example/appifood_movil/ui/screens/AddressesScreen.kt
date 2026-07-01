package com.example.appifood_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.appifood_movil.ui.components.BaseScreen
import com.example.appifood_movil.ui.viewmodel.AddressViewModel
import com.example.appifood_movil.domain.model.Address

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(
    navController: NavController,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    BaseScreen(
        title = "Mis Direcciones",
        navController = navController
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (addresses.isEmpty() && !isLoading) {
                EmptyAddressesState { showAddDialog = true }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(addresses) { address ->
                        AddressItem(
                            address = address,
                            onDelete = { viewModel.deleteAddress(address.id) }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Agregar dirección")
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showAddDialog) {
        AddAddressDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, addr, details ->
                viewModel.addAddress(title, addr, details)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddressItem(address: Address, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when(address.title.lowercase()) {
                    "casa" -> Icons.Default.Home
                    "trabajo" -> Icons.Default.Work
                    else -> Icons.Default.LocationOn
                },
                contentDescription = null,
                tint = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(address.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(address.address, color = Color.Gray, fontSize = 14.sp)
                if (!address.details.isNullOrBlank()) {
                    Text(address.details, color = Color.Gray, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun EmptyAddressesState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOff, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No tienes direcciones guardadas", color = Color.Gray)
        TextButton(onClick = onAdd) {
            Text("Agregar mi primera dirección", color = Color(0xFFD32F2F))
        }
    }
}

@Composable
fun AddAddressDialog(onDismiss: () -> Unit, onConfirm: (String, String, String?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Dirección") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Etiqueta (Ej: Casa)") })
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección completa") })
                OutlinedTextField(value = details, onValueChange = { details = it }, label = { Text("Detalles (Opcional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, address, details) },
                enabled = title.isNotBlank() && address.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
