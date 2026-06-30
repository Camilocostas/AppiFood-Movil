// data/repository/FoodRepositoryImpl.kt
package com.example.appifood_movil.data.repository

import android.util.Log
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.response.RestaurantDto
import com.example.appifood_movil.data.model.Adicion  // ✅ Importar desde data.model
import com.example.appifood_movil.domain.model.Dish
import com.example.appifood_movil.domain.model.FoodProduct
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.model.Review
import com.example.appifood_movil.domain.repository.FoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val firestore  : com.google.firebase.firestore.FirebaseFirestore =
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
) : FoodRepository {

    override fun getRestaurants(): Flow<List<Restaurant>> = flow {
        try {
            val firestoreRestaurants = getRestaurantsFromFirestore()
            if (firestoreRestaurants.isNotEmpty()) {
                emit(firestoreRestaurants)
                Log.d("FoodRepo", "✅ Restaurantes desde Firestore: ${firestoreRestaurants.size}")
            }

            val response = apiService.getRestaurants()
            if (response.isSuccessful) {
                val body = response.body()
                val domainList = body?.data?.map { it.toDomain() } ?: emptyList()
                if (domainList.isNotEmpty()) {
                    val combined = (firestoreRestaurants + domainList).distinctBy { it.id }
                    emit(combined)
                    Log.d("FoodRepo", "✅ Restaurantes combinados: ${combined.size}")
                }
            }
        } catch (e: Exception) {
            Log.e("FoodRepo", "❌ Error: ${e.message}")
            emit(emptyList())
        }
    }

    private suspend fun getRestaurantsFromFirestore(): List<Restaurant> {
        return try {
            Log.d("FoodRepo", "🔍 Buscando restaurantes en Firestore...")
            val snapshot = firestore.collection("restaurants").get().await()
            Log.d("FoodRepo", "📄 Documentos encontrados: ${snapshot.documents.size}")

            snapshot.documents.mapNotNull { doc ->
                val restaurantName = doc.getString("restaurantName") ?: ""
                val uid = doc.id
                val isActive = doc.getBoolean("isActive") ?: false
                val estado = doc.getString("estado") ?: ""

                if (!isActive && estado != "activo") {
                    Log.d("FoodRepo", "⏭️ Restaurante inactivo: $restaurantName")
                    return@mapNotNull null
                }

                Log.d("FoodRepo", "🏪 Restaurante encontrado: $restaurantName (UID: $uid)")

                val dishesList = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
                Log.d("FoodRepo", "🍽️ Platos encontrados: ${dishesList.size}")

                val dishes = dishesList.mapIndexed { index, dish ->
                    Dish(
                        name = dish["nombre"] as? String ?: "",
                        price = (dish["precio"] as? Number)?.toDouble() ?: 0.0,
                        imageRes = com.example.appifood_movil.R.drawable.burguer,
                        imageUrl = dish["imagenUrl"] as? String ?: ""
                    )
                }

                Restaurant(
                    id = uid.hashCode(),
                    name = restaurantName,
                    phone = doc.getString("phone") ?: "",
                    address = doc.getString("direccion") ?: "",
                    imageUrl = doc.getString("imagenPortada") ?: "",
                    imageRes = com.example.appifood_movil.R.drawable.restaurantechino,
                    schedule = doc.getString("horario") ?: "Horario no disponible",
                    hasDelivery = true,
                    rating = "4.5",
                    category = doc.getString("categoria") ?: "General",
                    deliveryTime = "30-45 min",
                    latitude = 0.0,
                    longitude = 0.0,
                    uid = uid,
                    estado = if (estado.isNotEmpty()) estado else "activo",
                    dishes = dishes,
                    reviews = emptyList()
                )
            }
        } catch (e: Exception) {
            Log.e("FoodRepo", "❌ Error obteniendo restaurantes: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getProducts(): List<FoodProduct> {
        return try {
            val firestoreRestaurants = getRestaurantsFromFirestore()
            val allProducts = mutableListOf<FoodProduct>()

            firestoreRestaurants.forEach { restaurant ->
                val platosConAdiciones = getPlatosWithAdiciones(restaurant.uid)
                platosConAdiciones.forEachIndexed { index, platoData ->
                    allProducts.add(
                        FoodProduct(
                            id = restaurant.id * 100 + index,
                            name = platoData.nombre,
                            price = platoData.precio,
                            imageRes = com.example.appifood_movil.R.drawable.burguer,
                            imagenUrl = restaurant.imageUrl,
                            category = restaurant.category,
                            description = platoData.descripcion,
                            restaurantId = restaurant.id,
                            disponible = platoData.disponible,
                            precioPromocion = platoData.precioPromocion,
                            descuento = platoData.descuento,
                            adiciones = platoData.adiciones
                        )
                    )
                }
            }
            allProducts
        } catch (e: Exception) {
            Log.e("FoodRepo", "Error obteniendo productos: ${e.message}")
            emptyList()
        }
    }

    private suspend fun getPlatosWithAdiciones(restauranteUid: String): List<PlatoData> {
        return try {
            val doc = firestore.collection("restaurants")
                .document(restauranteUid)
                .get()
                .await()

            val dishesList = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

            dishesList.map { dish ->
                PlatoData(
                    nombre = dish["nombre"] as? String ?: "",
                    descripcion = dish["descripcion"] as? String ?: "",
                    precio = (dish["precio"] as? Number)?.toDouble() ?: 0.0,
                    precioPromocion = (dish["precioPromocion"] as? Number)?.toDouble() ?: 0.0,
                    descuento = (dish["descuento"] as? Number)?.toInt() ?: 0,
                    disponible = dish["disponible"] as? Boolean ?: true,
                    adiciones = (dish["adiciones"] as? List<Map<String, Any>>)?.map {
                        Adicion(
                            nombre = it["nombre"] as? String ?: "",
                            precio = (it["precio"] as? Number)?.toDouble() ?: 0.0
                        )
                    } ?: emptyList()
                )
            }
        } catch (e: Exception) {
            Log.e("FoodRepo", "Error obteniendo platos con adiciones: ${e.message}")
            emptyList()
        }
    }

    data class PlatoData(
        val nombre: String = "",
        val descripcion: String = "",
        val precio: Double = 0.0,
        val precioPromocion: Double = 0.0,
        val descuento: Int = 0,
        val disponible: Boolean = true,
        val adiciones: List<Adicion> = emptyList()
    )

    override suspend fun getProductById(id: Int): FoodProduct? {
        return try {
            Log.d("FoodRepo", "🔍 Buscando producto ID: $id")
            val products = getProducts()
            Log.d("FoodRepo", "📦 Productos disponibles: ${products.size}")

            var product = products.find { it.id == id }
            if (product == null) {
                Log.d("FoodRepo", "⚠️ No encontrado por ID, buscando por nombre...")
                product = products.find { it.name.hashCode() == id }
            }

            Log.d("FoodRepo", "✅ Resultado: ${product?.name ?: "null"}")
            product
        } catch (e: Exception) {
            Log.e("FoodRepo", "❌ Error: ${e.message}")
            null
        }
    }

    override suspend fun searchRestaurants(query: String): List<Restaurant> {
        val restaurants = getRestaurantsFromFirestore()
        return restaurants.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
    }

    // Añade este método al FoodRepositoryImpl existente
// (no toques los métodos que ya existen)

    override suspend fun getProductFromFirestore(productId: Int): FoodProduct? {
        // ── Decodificar el productId ──────────────────────────────────
        // El id se genera como: restaurantId * 100 + dishIndex
        // Así podemos saber de qué restaurante y qué plato es
        val restaurantIndex = productId / 100   // índice en la lista local
        val dishIndex       = productId % 100   // índice del plato en dishes[]

        return try {
            // Buscar todos los restaurantes con rol "restaurant"
            val snapshot = firestore.collection("restaurants")
                .whereEqualTo("role", "restaurant")
                .get()
                .await()

            val docs = snapshot.documents

            // Verificar que el índice del restaurante sea válido
            if (restaurantIndex >= docs.size) {
                android.util.Log.w("FoodRepo", "restaurantIndex $restaurantIndex fuera de rango (${docs.size} restaurantes)")
                return null
            }

            val doc = docs[restaurantIndex]
            val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()

            if (dishIndex >= dishes.size) {
                android.util.Log.w("FoodRepo", "dishIndex $dishIndex fuera de rango (${dishes.size} platos)")
                return null
            }

            val dish = dishes[dishIndex]

            // Parsear adiciones
            val adicionesRaw = dish["adiciones"] as? List<Map<String, Any>> ?: emptyList()
            val adiciones = adicionesRaw.map { a ->
                com.example.appifood_movil.data.model.Adicion(
                    nombre = a["nombre"] as? String ?: "",
                    precio = (a["precio"] as? Number)?.toDouble() ?: 0.0
                )
            }

            FoodProduct(
                id               = productId,
                name             = dish["nombre"] as? String ?: "",
                price            = (dish["precio"] as? Number)?.toDouble() ?: 0.0,
                imageRes         = com.example.appifood_movil.R.drawable.burguer,
                category         = dish["categoria"] as? String ?: "",
                description      = dish["descripcion"] as? String ?: "",
                imagenUrl        = dish["imagenUrl"] as? String ?: "",
                precioPromocion  = (dish["precioPromocion"] as? Number)?.toDouble() ?: 0.0,
                descuento        = (dish["descuento"] as? Number)?.toInt() ?: 0,
                disponible       = dish["disponible"] as? Boolean ?: true,
                adiciones        = adiciones,
                restaurantId     = restaurantIndex,
                restaurantName   = doc.getString("restaurantName") ?: ""
            )
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "Error cargando plato $productId de Firestore", e)
            // Fallback a FakeData
            getProductById(productId)
        }
    }

    override suspend fun getRestaurantById(id: Int): Restaurant? {
        return try {
            val restaurants = getRestaurantsFromFirestore()
            restaurants.find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    private fun RestaurantDto.toDomain(): Restaurant = Restaurant(
        id = this.id,
        name = this.name,
        phone = this.phone ?: "",
        address = this.address ?: "",
        imageUrl = this.image ?: this.logo ?: "",
        imageRes = com.example.appifood_movil.R.drawable.restaurantechino,
        schedule = this.time ?: "Horario no disponible",
        hasDelivery = (this.deliveryCost ?: 0.0) > 0.0,
        rating = this.averageRating?.takeIf { it > 0 }?.toString()
            ?: this.rating ?: "4.5",
        category = this.category?.ifBlank { "General" } ?: "General",
        latitude = this.latitude ?: this.lat ?: 0.0,
        longitude = this.longitude ?: this.lng ?: 0.0,
        uid = this.uid ?: "",
        estado = this.estado ?: "activo",
        dishes = this.dishes?.map { d ->
            Dish(
                name = d.name ?: "",
                price = d.price ?: 0.0,
                imageRes = com.example.appifood_movil.R.drawable.burguer
            )
        } ?: emptyList(),
        reviews = this.reviews?.map { r ->
            Review(
                user = r.user ?: "",
                comment = r.comment ?: "",
                rating = r.rating ?: 5
            )
        } ?: emptyList()
    )
}