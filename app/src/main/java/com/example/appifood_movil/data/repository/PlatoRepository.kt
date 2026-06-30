package com.example.appifood_movil.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.example.appifood_movil.data.model.Adicion
import android.app.Application
import com.example.appifood_movil.service.CloudinaryService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

data class Plato(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val precioPromocion: Double = 0.0,
    val descuento: Int = 0,
    val categoria: String = "General",  // ✅ Este campo debe existir
    val imagenUrl: String = "",
    val disponible: Boolean = true,
    val adiciones: List<Adicion> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)


@Singleton
class PlatoRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val application: Application
) {

    private val TAG = "PlatoRepository"

    fun getPlatos(restauranteId: String): Flow<List<Plato>> = flow {
        try {
            val doc = firestore.collection("restaurants")
                .document(restauranteId)
                .get()
                .await()

            val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
            val platos = dishes.map { dish ->
                Plato(
                    id = dish["id"] as? String ?: "",
                    nombre = dish["nombre"] as? String ?: "",
                    descripcion = dish["descripcion"] as? String ?: "",
                    precio = (dish["precio"] as? Number)?.toDouble() ?: 0.0,
                    precioPromocion = (dish["precioPromocion"] as? Number)?.toDouble() ?: 0.0,
                    descuento = (dish["descuento"] as? Number)?.toInt() ?: 0,
                    categoria = dish["categoria"] as? String ?: "General",
                    imagenUrl = dish["imagenUrl"] as? String ?: "",
                    disponible = dish["disponible"] as? Boolean ?: true,
                    adiciones = (dish["adiciones"] as? List<Map<String, Any>>)?.map {
                        Adicion(
                            nombre = it["nombre"] as? String ?: "",
                            precio = (it["precio"] as? Number)?.toDouble() ?: 0.0
                        )
                    } ?: emptyList(),
                    createdAt = (dish["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                )
            }
            emit(platos)
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando platos: ${e.message}")
            emit(emptyList())
        }
    }

    // ✅ Guardar plato (con imagen)
    suspend fun savePlato(
        restauranteId: String,
        plato: Plato,
        imagenUri: Uri?
    ) {
        try {
            var imagenUrl = plato.imagenUrl

            // Si hay imagen nueva, subir a Cloudinary
            if (imagenUri != null) {
                val uploadedUrl = CloudinaryService.uploadImage(
                    context = application,
                    imageUri = imagenUri,
                    folder = "restaurantes/$restauranteId/platos",
                    publicId = plato.id.ifEmpty { System.currentTimeMillis().toString() }
                )
                if (uploadedUrl != null) {
                    imagenUrl = uploadedUrl
                    Log.d(TAG, "✅ Imagen subida a Cloudinary: $imagenUrl")
                } else {
                    Log.e(TAG, "❌ Falló la subida de imagen a Cloudinary")
                }
            }

            // Preparar el mapa del plato (CON ID INCLUIDO)
            val platoId = if (plato.id.isNotEmpty()) plato.id else System.currentTimeMillis().toString()
            val platoMap = mapOf(
                "id" to platoId,  // ✅ ID incluido siempre
                "nombre" to plato.nombre,
                "descripcion" to plato.descripcion,
                "precio" to plato.precio,
                "precioPromocion" to plato.precioPromocion,
                "descuento" to plato.descuento,
                "categoria" to plato.categoria,
                "imagenUrl" to imagenUrl,
                "disponible" to plato.disponible,
                "adiciones" to plato.adiciones.map { a ->
                    mapOf("nombre" to a.nombre, "precio" to a.precio)
                },
                "createdAt" to plato.createdAt
            )

            // Actualizar Firestore
            val docRef = firestore.collection("restaurants")
                .document(restauranteId)

            val doc = docRef.get().await()
            val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

            // ✅ Lógica de actualización corregida
            val updatedDishes = if (plato.id.isNotEmpty()) {
                // ACTUALIZAR: buscar por ID y reemplazar
                val index = dishes.indexOfFirst { it["id"] == plato.id }
                if (index != -1) {
                    dishes.toMutableList().apply {
                        this[index] = platoMap
                    }
                } else {
                    // Si no se encuentra, agregar como nuevo (por seguridad)
                    dishes + platoMap
                }
            } else {
                // AGREGAR NUEVO
                dishes + platoMap
            }

            docRef.update("dishes", updatedDishes).await()
            Log.d(TAG, "✅ Plato guardado correctamente. ID: $platoId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error guardando plato: ${e.message}")
        }
    }

    suspend fun toggleDisponible(restauranteId: String, platoId: String) {
        val index = platoId.replace("dish_", "").toIntOrNull() ?: return
        val docRef = firestore.collection("restaurants").document(restauranteId)
        val doc = docRef.get().await()

        val currentDishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
        if (index >= currentDishes.size) return

        val updatedDishes = currentDishes.toMutableList()
        val dish = updatedDishes[index].toMutableMap()
        val currentDisponible = dish["disponible"] as? Boolean ?: true
        dish["disponible"] = !currentDisponible
        updatedDishes[index] = dish

        docRef.update("dishes", updatedDishes).await()
    }

    suspend fun setPromocion(
        restauranteId: String,
        platoId: String,
        precioPromocion: Double,
        descuento: Int
    ) {
        try {
            val docRef = firestore.collection("restaurants").document(restauranteId)
            val doc = docRef.get().await()
            val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

            val updatedDishes = dishes.map { dish ->
                if (dish["id"] == platoId) {
                    dish + mapOf(
                        "precioPromocion" to precioPromocion,
                        "descuento" to descuento
                    )
                } else {
                    dish
                }
            }

            docRef.update("dishes", updatedDishes).await()
            Log.d("PlatoRepository", "✅ Promoción aplicada: $platoId")
        } catch (e: Exception) {
            Log.e("PlatoRepository", "❌ Error set promocion: ${e.message}")
        }
    }

    suspend fun deletePlato(restauranteId: String, platoId: String) {
        try {
            val docRef = firestore.collection("restaurants")
                .document(restauranteId)

            val doc = docRef.get().await()
            val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

            // Filtrar el plato a eliminar por ID
            val updatedDishes = dishes.filter { it["id"] != platoId }

            if (updatedDishes.size == dishes.size) {
                Log.w(TAG, "⚠️ Plato no encontrado para eliminar: $platoId")
            } else {
                docRef.update("dishes", updatedDishes).await()
                Log.d(TAG, "✅ Plato eliminado: $platoId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando plato: ${e.message}")
        }
    }

    suspend fun uploadPlatoImage(restauranteId: String, imageUri: Uri): String {
        val timestamp = System.currentTimeMillis()
        val ref = storage.reference
            .child("restaurantes/$restauranteId/platos/$timestamp.jpg")

        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}