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

data class Plato(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val precioPromocion: Double = 0.0,
    val descuento: Int = 0,
    val categoria: String = "",
    val imagenUrl: String = "",
    val disponible: Boolean = true,
    val adiciones: List<Adicion> = emptyList(),  // ✅ AGREGAR
    val createdAt: Long = System.currentTimeMillis()
)

@Singleton
class PlatoRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    fun getPlatos(restauranteId: String): Flow<List<Plato>> = flow {
        try {
            val doc = firestore.collection("restaurants")
                .document(restauranteId)
                .get()
                .await()

            if (doc.exists()) {
                val dishesList = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

                val platos = dishesList.mapIndexed { index, dish ->
                    Plato(
                        id = "dish_$index",
                        nombre = dish["nombre"] as? String ?: "",
                        descripcion = dish["descripcion"] as? String ?: "",
                        precio = (dish["precio"] as? Number)?.toDouble() ?: 0.0,
                        precioPromocion = (dish["precioPromocion"] as? Number)?.toDouble() ?: 0.0,
                        descuento = (dish["descuento"] as? Number)?.toInt() ?: 0,
                        categoria = dish["categoria"] as? String ?: "",
                        imagenUrl = dish["imagenUrl"] as? String ?: "",
                        disponible = dish["disponible"] as? Boolean ?: true,
                        // ✅ Leer adiciones
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
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("PlatoRepository", "Error: ${e.message}")
            emit(emptyList())
        }
    }

    suspend fun savePlato(restauranteId: String, plato: Plato, imagenUri: Uri?) {
        try {
            val docRef = firestore.collection("restaurants").document(restauranteId)
            val doc = docRef.get().await()

            val currentDishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

            val newDish = mutableMapOf<String, Any>(
                "nombre" to plato.nombre,
                "descripcion" to plato.descripcion,
                "precio" to plato.precio,
                "precioPromocion" to plato.precioPromocion,
                "descuento" to plato.descuento,
                "categoria" to plato.categoria,
                "disponible" to plato.disponible,
                // ✅ Guardar adiciones
                "adiciones" to plato.adiciones.map { mapOf("nombre" to it.nombre, "precio" to it.precio) },
                "createdAt" to System.currentTimeMillis()
            )

            imagenUri?.let { uri ->
                val imageUrl = uploadPlatoImage(restauranteId, uri)
                newDish["imagenUrl"] = imageUrl
            }

            val updatedDishes = currentDishes + newDish
            docRef.update("dishes", updatedDishes).await()

        } catch (e: Exception) {
            Log.e("PlatoRepository", "Error guardando: ${e.message}")
            throw e
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
        val index = platoId.replace("dish_", "").toIntOrNull() ?: return
        val docRef = firestore.collection("restaurants").document(restauranteId)
        val doc = docRef.get().await()

        val currentDishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
        if (index >= currentDishes.size) return

        val updatedDishes = currentDishes.toMutableList()
        val dish = updatedDishes[index].toMutableMap()
        dish["precioPromocion"] = precioPromocion
        dish["descuento"] = descuento
        updatedDishes[index] = dish

        docRef.update("dishes", updatedDishes).await()
    }

    suspend fun deletePlato(restauranteId: String, platoId: String) {
        val index = platoId.replace("dish_", "").toIntOrNull() ?: return
        val docRef = firestore.collection("restaurants").document(restauranteId)
        val doc = docRef.get().await()

        val currentDishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
        if (index >= currentDishes.size) return

        val updatedDishes = currentDishes.toMutableList()
        updatedDishes.removeAt(index)

        docRef.update("dishes", updatedDishes).await()
    }

    suspend fun uploadPlatoImage(restauranteId: String, imageUri: Uri): String {
        val timestamp = System.currentTimeMillis()
        val ref = storage.reference
            .child("restaurantes/$restauranteId/platos/$timestamp.jpg")

        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}