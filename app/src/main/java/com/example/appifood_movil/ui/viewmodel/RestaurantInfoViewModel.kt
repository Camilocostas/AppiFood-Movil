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
import java.net.URLEncoder
import java.net.URL
import com.example.appifood_movil.service.CloudinaryService
import android.util.Log
import org.json.JSONArray
import java.net.HttpURLConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.app.Application


data class RestaurantInfo(
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val horario: String = "",
    val imagenPortada: String = "",
    val fotosGaleria: List<String> = emptyList(),
    val latitude: Double = 0.0,   // ✅ Nuevo
    val longitude: Double = 0.0   // ✅ Nuevo
)

@HiltViewModel
class RestaurantInfoViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val application: Application
) : ViewModel() {

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress: StateFlow<Int> = _uploadProgress
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
                        fotosGaleria = (doc.get("fotosGaleria") as? List<String>) ?: emptyList(),
                        latitude = (doc.get("latitude") as? Number)?.toDouble() ?: 0.0,  // ✅
                        longitude = (doc.get("longitude") as? Number)?.toDouble() ?: 0.0 // ✅
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
                // 🔍 Geocodificar la dirección para obtener coordenadas
                var latitude = 0.0
                var longitude = 0.0
                if (direccion.isNotBlank()) {
                    val coords = geocodeAddress(direccion)
                    if (coords != null) {
                        latitude = coords.first
                        longitude = coords.second
                    }
                }

                val updates = mapOf(
                    "restaurantName" to nombre,
                    "descripcion" to descripcion,
                    "categoria" to categoria,
                    "direccion" to direccion,
                    "phone" to telefono,
                    "horario" to horario,
                    "latitude" to latitude,
                    "longitude" to longitude
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
                    horario = horario,
                    latitude = latitude,
                    longitude = longitude
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Construir la URL
                val fullAddress = if (address.contains("Popayán", ignoreCase = true)) {
                    address
                } else {
                    "$address, Popayán"
                }
                val encoded = URLEncoder.encode(fullAddress, "UTF-8")
                val url = "https://nominatim.openstreetmap.org/search?q=$encoded&format=json&limit=1"
                Log.d("Geocoding", "🌐 URL: $url")

                // 2. Crear conexión y configurar User-Agent (OBLIGATORIO)
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "AppiFood-Movil/1.0 (contacto@tuapp.com)") // ✅ CAMBIA esto
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                // 3. Leer respuesta
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("Geocoding", "📄 Respuesta: ${response.take(200)}...")

                // 4. Parsear JSON
                val json = JSONArray(response)
                if (json.length() > 0) {
                    val obj = json.getJSONObject(0)
                    val lat = obj.getString("lat").toDouble()
                    val lng = obj.getString("lon").toDouble()
                    Log.d("Geocoding", "✅ Coordenadas: $lat, $lng")
                    lat to lng
                } else {
                    Log.e("Geocoding", "❌ No se encontró la dirección")
                    null
                }
            } catch (e: Exception) {
                Log.e("Geocoding", "❌ Error: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
    // ── SUBIR IMAGEN DE PORTADA (con Cloudinary) ──────────────────
    fun uploadPortadaImage(restauranteId: String, imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _uploadProgress.value = 0
            try {
                val url = CloudinaryService.uploadImage(
                    context = application,
                    imageUri = imageUri,
                    folder = "restaurantes/$restauranteId",
                    publicId = "portada"
                )
                if (url != null) {
                    firestore.collection("restaurants")
                        .document(restauranteId)
                        .update("imagenPortada", url)
                        .await()

                    val current = _restaurantInfo.value
                    _restaurantInfo.value = current?.copy(imagenPortada = url)
                    Log.d("Upload", "✅ Portada actualizada")
                    _uploadProgress.value = 100
                } else {
                    Log.e("Upload", "❌ Falló la subida de portada")
                    _uploadProgress.value = -1
                }
            } catch (e: Exception) {
                Log.e("Upload", "❌ Error: ${e.message}", e)
                _uploadProgress.value = -1
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadGaleriaImage(restauranteId: String, imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _uploadProgress.value = 0
            try {
                val timestamp = System.currentTimeMillis()
                val url = CloudinaryService.uploadImage(
                    context = application,
                    imageUri = imageUri,
                    folder = "restaurantes/$restauranteId/galeria",
                    publicId = "foto_$timestamp"
                )
                if (url != null) {
                    val current = _restaurantInfo.value
                    val currentPhotos = current?.fotosGaleria ?: emptyList()
                    val newPhotos = currentPhotos + url

                    firestore.collection("restaurants")
                        .document(restauranteId)
                        .update("fotosGaleria", newPhotos)
                        .await()

                    _restaurantInfo.value = current?.copy(fotosGaleria = newPhotos)
                    Log.d("Upload", "✅ Foto de galería agregada")
                    _uploadProgress.value = 100
                } else {
                    Log.e("Upload", "❌ Falló la subida de foto")
                    _uploadProgress.value = -1
                }
            } catch (e: Exception) {
                Log.e("Upload", "❌ Error: ${e.message}", e)
                _uploadProgress.value = -1
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun removeGaleriaImage(restauranteId: String, imageUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Extraer public_id de la URL
                val publicId = extractPublicId(imageUrl)
                if (publicId != null) {
                    CloudinaryService.deleteImage(publicId)
                }

                val current = _restaurantInfo.value
                val currentPhotos = current?.fotosGaleria ?: emptyList()
                val newPhotos = currentPhotos.filter { it != imageUrl }

                firestore.collection("restaurants")
                    .document(restauranteId)
                    .update("fotosGaleria", newPhotos)
                    .await()

                _restaurantInfo.value = current?.copy(fotosGaleria = newPhotos)
                Log.d("Upload", "✅ Foto eliminada")
            } catch (e: Exception) {
                Log.e("Upload", "❌ Error: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── EXTRACCIÓN DE PUBLIC_ID DESDE URL DE CLOUDINARY ──────────
    private fun extractPublicId(url: String): String? {
        return try {
            // Ejemplo: https://res.cloudinary.com/cloud_name/image/upload/v123456/restaurantes/uid/portada.jpg
            val parts = url.split("/")
            val uploadIndex = parts.indexOf("upload")
            if (uploadIndex != -1 && uploadIndex + 1 < parts.size) {
                // Tomamos la parte después de upload/ (incluye carpeta y nombre sin extensión)
                val pathWithVersion = parts.subList(uploadIndex + 1, parts.size).joinToString("/")
                // Eliminar la versión (v123456/) si existe
                val cleanPath = pathWithVersion.replace(Regex("^v\\d+/"), "")
                // Eliminar extensión
                cleanPath.substringBeforeLast(".")
            } else null
        } catch (e: Exception) {
            null
        }
    }
}