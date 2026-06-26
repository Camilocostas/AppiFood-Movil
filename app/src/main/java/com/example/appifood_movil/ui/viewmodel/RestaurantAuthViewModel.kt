// ui/viewmodel/RestaurantAuthViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RestaurantAuthViewModel @Inject constructor() : ViewModel() {

    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // isSuccess dispara la navegación al dashboard
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // ── Login de restaurante ──────────────────────────────────────
    fun loginRestaurant(email: String, password: String) {
        _isLoading.value = true
        _error.value     = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    // Verificar que el usuario tiene rol "restaurant"
                    firestore.collection("restaurants").document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                _isSuccess.value = true
                            } else {
                                auth.signOut()
                                _error.value = "Esta cuenta no es de restaurante"
                            }
                        }
                        .addOnFailureListener {
                            _error.value = "Error verificando rol de restaurante"
                        }
                } else {
                    _error.value = when {
                        task.exception?.message?.contains("no user") == true ->
                            "Correo no registrado"
                        task.exception?.message?.contains("password") == true ->
                            "Contraseña incorrecta"
                        else -> task.exception?.message ?: "Error al iniciar sesión"
                    }
                }
            }
    }

    // ── Registro de restaurante ───────────────────────────────────
    // Guarda en Firebase Auth + colección "restaurants" en Firestore
    fun registerRestaurant(
        ownerName      : String,
        restaurantName : String,
        email          : String,
        phone          : String,
        documentNumber : String,
        password       : String
    ) {
        _isLoading.value = true
        _error.value     = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid  = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // Datos del restaurante en Firestore
                    val restaurantData = hashMapOf(
                        "uid"            to uid,
                        "ownerName"      to ownerName,
                        "restaurantName" to restaurantName,
                        "email"          to email,
                        "phone"          to phone,
                        "documentNumber" to documentNumber,
                        "role"           to "restaurant",
                        "createdAt"      to System.currentTimeMillis(),
                        "isActive"       to true,
                        "rating"         to 0.0,
                        "dishes"         to emptyList<Any>(),
                        "photos"         to emptyList<Any>()
                    )

                    // Guardar en colección "restaurants"
                    firestore.collection("restaurants").document(uid)
                        .set(restaurantData)
                        .addOnSuccessListener {
                            _isLoading.value = false
                            _isSuccess.value = true
                            Log.d("RestaurantAuth", "Restaurante registrado: $uid")
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            // Si Firestore falla, eliminar la cuenta de Auth
                            auth.currentUser?.delete()
                            _error.value = "Error guardando datos: ${e.message}"
                        }
                } else {
                    _isLoading.value = false
                    _error.value = when {
                        task.exception?.message?.contains("already in use") == true ->
                            "Este correo ya está registrado"
                        task.exception?.message?.contains("weak") == true ->
                            "La contraseña es muy débil"
                        else -> task.exception?.message ?: "Error al registrar"
                    }
                }
            }
    }

    fun clearError() { _error.value = null }
}