package com.example.appifood_movil.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.restaurants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

data class SearchCriteria(
    val query: String = "",
    val category: String? = null,
    val maxPrice: Double = 50000.0,
    val radiusKm: Double = 5.0,
    val userLocation: Location? = null
)

class SearchViewModel : ViewModel() {
    private val _criteria = MutableStateFlow(SearchCriteria(maxPrice = 50000.0))
    val criteria = _criteria.asStateFlow()

    val filteredRestaurants = _criteria.map { c ->
        restaurants.filter { rest ->
            // 1. Filtro por nombre o categoría (el "Qué se te antoja")
            val matchesQuery = c.query.isBlank() ||
                    rest.name.contains(c.query, true) ||
                    rest.category.contains(c.query, true) ||
                    rest.dishes.any { it.name.contains(c.query, true) }

            // 2. Filtro por precio (Busca si algún plato está por debajo del maxPrice)
            // Ya que dish.price es un Double, simplemente compáralo:
            val matchesPrice = rest.dishes.any { dish ->
                dish.price <= c.maxPrice
            }

            // 3. Filtro por distancia (el que ya tenías)
            // ... dentro de filteredRestaurants.map { ... } ...

// 3. Filtro por distancia mejorado
            val matchesDistance = if (c.userLocation != null) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    c.userLocation.latitude,
                    c.userLocation.longitude,
                    rest.latitude,
                    rest.longitude,
                    results
                )
                (results[0] / 1000.0) <= c.radiusKm
            } else {
                // AQUÍ ESTÁ EL CAMBIO:
                // Si no hay ubicación (emulador), devolvemos 'true' para que no oculte nada
                // Opcionalmente, podrías poner aquí tus coordenadas de Popayán para probar:
                // val latPopayan = 2.4419
                // val lonPopayan = -76.6062
                true
            }

            matchesQuery && matchesPrice && matchesDistance
        }
    }

    fun updateQuery(q: String) { _criteria.value = _criteria.value.copy(query = q) }
    fun updateCategory(cat: String?) { _criteria.value = _criteria.value.copy(category = cat) }
    fun updateLocation(loc: Location) { _criteria.value = _criteria.value.copy(userLocation = loc) }
    fun updateRadius(radius: Double) { _criteria.value = _criteria.value.copy(radiusKm = radius) }
    fun updateMaxPrice(price: Double) { _criteria.value = _criteria.value.copy(maxPrice = price)
    }
}