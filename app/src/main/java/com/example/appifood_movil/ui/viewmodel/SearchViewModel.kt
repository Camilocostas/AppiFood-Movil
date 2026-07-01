package com.example.appifood_movil.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.LocationManager
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.Normalizer
import javax.inject.Inject

data class SearchCriteria(
    val query: String = "",
    val category: String? = null,
    val maxPrice: Double = 100000.0,
    val radiusKm: Double = 500.0,
    val userLocation: Location? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _criteria = MutableStateFlow(SearchCriteria())
    val criteria = _criteria.asStateFlow()

    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        foodRepository.getRestaurants(),
        _criteria
    ) { restaurants, c ->
        val queryTokens = normalize(c.query)
            .split(" ")
            .filter { it.isNotBlank() }

        restaurants.filter { rest ->
            val matchesQuery = queryTokens.isEmpty() || queryTokens.any { token ->
                normalize(rest.name).contains(token) ||
                        normalize(rest.category).contains(token) ||
                        normalize(rest.description).contains(token) ||
                        rest.dishes.any { dish -> normalize(dish.name).contains(token) }
            }

            // Precio regular del plato (no promoción) — al menos un plato
            // dentro del presupuesto, o restaurante sin platos cargados aún
            val matchesPrice = rest.dishes.isEmpty() ||
                    rest.dishes.any { dish -> dish.price <= c.maxPrice }

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
                true
            }

            matchesQuery && matchesPrice && matchesDistance
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Normaliza texto para búsqueda: minúsculas y sin tildes/diacríticos.
     * Permite que "café" encuentre "cafe" y viceversa.
     */
    private fun normalize(text: String): String {
        val lower = text.lowercase()
        val normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{Mn}+"), "")
    }

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