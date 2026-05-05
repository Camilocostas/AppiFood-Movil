package com.example.appifood_movil.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.LocationManager
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchCriteria(
    val query: String = "",
    val category: String? = null,
    val maxPrice: Double = 50000.0,
    val radiusKm: Double = 5.0,
    val userLocation: Location? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _criteria = MutableStateFlow(SearchCriteria(maxPrice = 50000.0))
    val criteria = _criteria.asStateFlow()

    // Combinamos el flujo de criterios con el flujo de datos del repositorio
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        foodRepository.getRestaurants(),
        _criteria
    ) { restaurants, c ->
        restaurants.filter { rest ->
            // 1. Filtro por nombre o categoría
            val matchesQuery = c.query.isBlank() ||
                    rest.name.contains(c.query, true) ||
                    rest.category.contains(c.query, true) ||
                    rest.dishes.any { it.name.contains(c.query, true) }

            // 2. Filtro por precio
            val matchesPrice = rest.dishes.any { dish ->
                dish.price <= c.maxPrice
            }

            // 3. Filtro por distancia
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
                true
            }

            matchesQuery && matchesPrice && matchesDistance
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchUserLocation() {
        locationManager.getCurrentLocation { loc ->
            updateLocation(loc)
        }
    }

    fun updateQuery(q: String) { _criteria.value = _criteria.value.copy(query = q) }
    fun updateCategory(cat: String?) { _criteria.value = _criteria.value.copy(category = cat) }
    fun updateLocation(loc: Location) { _criteria.value = _criteria.value.copy(userLocation = loc) }
    fun updateRadius(radius: Double) { _criteria.value = _criteria.value.copy(radiusKm = radius) }
    fun updateMaxPrice(price: Double) { _criteria.value = _criteria.value.copy(maxPrice = price) }
}
