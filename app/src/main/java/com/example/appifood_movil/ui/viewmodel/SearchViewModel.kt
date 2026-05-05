package com.example.appifood_movil.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.LocationManager
import com.example.appifood_movil.data.restaurants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
    private val locationManager: LocationManager
) : ViewModel() {
    private val _criteria = MutableStateFlow(SearchCriteria(maxPrice = 50000.0))
    val criteria = _criteria.asStateFlow()

    val filteredRestaurants = _criteria.map { c ->
        restaurants.filter { rest ->
            val matchesQuery = c.query.isBlank() ||
                    rest.name.contains(c.query, true) ||
                    rest.category.contains(c.query, true) ||
                    rest.dishes.any { it.name.contains(c.query, true) }

            val matchesPrice = rest.dishes.any { dish ->
                dish.price <= c.maxPrice
            }

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
    }

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
