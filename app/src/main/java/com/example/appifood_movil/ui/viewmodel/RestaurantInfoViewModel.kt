package com.example.appifood_movil.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class RestaurantInfo(
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val horario: String = "",
    val imagenPortada: String = "",
    val fotosGaleria: List<String> = emptyList()
)

@HiltViewModel
class RestaurantInfoViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _restaurantInfo = MutableStateFlow<RestaurantInfo?>(null)
    val restaurantInfo: StateFlow<RestaurantInfo?> = _restaurantInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadRestaurantInfo(restauranteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("restaurants")
                    .document(restauranteId)
                    .get()
                    .await()

                if (doc.exists()) {
                    val info = RestaurantInfo(
                        nombre = doc.getString("restaurantName") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        categoria = doc.getString("categoria") ?: "",
                        direccion = doc.getString("direccion") ?: "",
                        telefono = doc.getString("phone") ?: "",
                        horario = doc.getString("horario") ?: "",
                        imagenPortada = doc.getString("imagenPortada") ?: "",
                        fotosGaleria = (doc.get("fotosGaleria") as? List<String>) ?: emptyList()
                    )
                    _restaurantInfo.value = info
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRestaurantInfo(
        restauranteId: String,
        nombre: String,
        descripcion: String,
        categoria: String,
        direccion: String,
        telefono: String,
        horario: String
    ) {
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "restaurantName" to nombre,
                    "descripcion" to descripcion,
                    "categoria" to categoria,
                    "direccion" to direccion,
                    "phone" to telefono,
                    "horario" to horario
                )
                firestore.collection("restaurants")
                    .document(restauranteId)
                    .update(updates)
                    .await()

                val current = _restaurantInfo.value
                _restaurantInfo.value = current?.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    categoria = categoria,
                    direccion = direccion,
                    telefono = telefono,
                    horario = horario
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ Estas funciones ya están dentro de viewModelScope
    fun uploadPortadaImage(restauranteId: String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val ref = storage.reference
                    .child("restaurantes/$restauranteId/portada.jpg")
                ref.putFile(imageUri).await()
                val url = ref.downloadUrl.await().toString()

                firestore.collection("restaurants")
                    .document(restauranteId)
                    .update("imagenPortada", url)
                    .await()

                val current = _restaurantInfo.value
                _restaurantInfo.value = current?.copy(imagenPortada = url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadGaleriaImage(restauranteId: String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val timestamp = System.currentTimeMillis()
                val ref = storage.reference
                    .child("restaurantes/$restauranteId/galeria/$timestamp.jpg")
                ref.putFile(imageUri).await()
                val url = ref.downloadUrl.await().toString()

                val current = _restaurantInfo.value
                val currentPhotos = current?.fotosGaleria ?: emptyList()
                val newPhotos = currentPhotos + url

                firestore.collection("restaurants")
                    .document(restauranteId)
                    .update("fotosGaleria", newPhotos)
                    .await()

                _restaurantInfo.value = current?.copy(fotosGaleria = newPhotos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeGaleriaImage(restauranteId: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                val current = _restaurantInfo.value
                val currentPhotos = current?.fotosGaleria ?: emptyList()
                val newPhotos = currentPhotos.filter { it != imageUrl }

                firestore.collection("restaurants")
                    .document(restauranteId)
                    .update("fotosGaleria", newPhotos)
                    .await()

                _restaurantInfo.value = current?.copy(fotosGaleria = newPhotos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}