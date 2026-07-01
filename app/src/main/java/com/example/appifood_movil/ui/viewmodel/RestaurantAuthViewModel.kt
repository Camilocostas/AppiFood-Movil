package com.example.appifood_movil.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.local.TokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RestaurantAuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun loginRestaurant(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("Error de autenticación")

                val doc = firestore.collection("restaurants").document(uid).get().await()
                if (!doc.exists()) {
                    throw Exception("Esta cuenta no tiene rol de restaurante")
                }

                val token = auth.currentUser?.getIdToken(true)?.await()?.token ?: ""
                tokenManager.saveToken(token)

                _isSuccess.value = true

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al iniciar sesión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerRestaurant(
        ownerName: String,
        restaurantName: String,
        email: String,
        phone: String,
        documentNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("Error al crear usuario")

                // En registerRestaurant()
                val restaurantData = hashMapOf(
                    "ownerName" to ownerName,
                    "restaurantName" to restaurantName,
                    "email" to email,
                    "phone" to phone,
                    "documentNumber" to documentNumber,
                    "uid" to uid,
                    "rol" to "restaurante",
                    "estado" to "activo",   // ✅ Agregar
                    "isActive" to true,     // ✅ Agregar
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("restaurants").document(uid).set(restaurantData).await()

                val token = auth.currentUser?.getIdToken(true)?.await()?.token ?: ""
                tokenManager.saveToken(token)

                _isSuccess.value = true

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al registrar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ MÉTODO PARA SUBIR IMÁGENES (dentro del ViewModel)
    suspend fun uploadProductImage(
        restauranteId: String,
        productId: String,
        imageUri: Uri
    ): Result<String> {
        return try {
            val ref = storage.reference.child("restaurants/$restauranteId/products/$productId.jpg")

            // Subir imagen
            ref.putFile(imageUri).await()

            // Obtener URL de descarga
            val url = ref.downloadUrl.await().toString()

            // Guardar URL en Firestore
            val updates = mapOf("imageUrl" to url)
            firestore.collection("restaurants")
                .document(restauranteId)
                .collection("products")
                .document(productId)
                .update(updates)
                .await()

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}