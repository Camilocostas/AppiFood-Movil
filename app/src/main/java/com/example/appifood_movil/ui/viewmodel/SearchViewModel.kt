package com.example.appifood_movil.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.LocationManager
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchCriteria(
    val query: String = "",
    val category: String? = null,
    val maxPrice: Double = 100000.0, // Precio máximo inicial alto para mostrar todo al inicio
    val radiusKm: Double = 500.0,    // Radio inicial amplio para evitar listas vacías por ubicación
    val userLocation: Location? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _criteria = MutableStateFlow(SearchCriteria())
    val criteria = _criteria.asStateFlow()

    // Combinamos el flujo de restaurantes del repo con los criterios de búsqueda
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        foodRepository.getRestaurants(),
        _criteria
    ) { restaurants, c ->
        restaurants.filter { rest ->
            // 1. Filtro por búsqueda de texto (Nombre, Categoría o Platos)
            val matchesQuery = c.query.isBlank() ||
                    rest.name.contains(c.query, true) ||
                    rest.category.contains(c.query, true) ||
                    rest.dishes.any { it.name.contains(c.query, true) }

            // 2. Filtro por precio máximo
            // Si el restaurante tiene platos, verificamos si alguno entra en el presupuesto.
            // Si no tiene platos (datos de API parciales), lo mostramos para no ser tan restrictivos.
            val matchesPrice = rest.dishes.isEmpty() || rest.dishes.any { dish ->
                dish.price <= c.maxPrice
            }

            // 3. Filtro por distancia
            // Solo filtramos por distancia si tenemos la ubicación del usuario y el radio no es el máximo (500)
            val matchesDistance = if (c.userLocation != null && c.radiusKm < 500) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    c.userLocation.latitude,
                    c.userLocation.longitude,
                    rest.latitude,
                    rest.longitude,
                    results
                )
                val distanceInKm = results[0] / 1000.0
                distanceInKm <= c.radiusKm
            } else {
                true // Si no hay ubicación o el radio es "ilimitado" (500), mostramos todo
            }

            matchesQuery && matchesPrice && matchesDistance
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchUserLocation() {
        locationManager.getCurrentLocation { loc ->
            updateLocation(loc)
        }
    }

    fun updateQuery(q: String) {
        _criteria.value = _criteria.value.copy(query = q)
    }

    fun updateLocation(loc: Location) {
        _criteria.value = _criteria.value.copy(userLocation = loc)
    }

    fun updateRadius(radius: Double) {
        _criteria.value = _criteria.value.copy(radiusKm = radius)
    }

    fun updateMaxPrice(price: Double) {
        _criteria.value = _criteria.value.copy(maxPrice = price)
    }
}
