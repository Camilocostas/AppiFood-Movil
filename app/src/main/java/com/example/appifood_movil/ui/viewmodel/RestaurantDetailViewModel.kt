package com.example.appifood_movil.ui.viewmodel// ui/viewmodel/RestaurantDetailViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.Review
import com.example.appifood_movil.data.repository.Plato
import com.example.appifood_movil.data.repository.PlatoRepository
import com.example.appifood_movil.data.repository.ReviewRepository  // ✅ Importar
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val platoRepository: PlatoRepository,
    private val reviewRepository: ReviewRepository  // ✅ Inyectar ReviewRepository
) : ViewModel() {

    private val _restaurant = MutableStateFlow<Restaurant?>(null)
    val restaurant: StateFlow<Restaurant?> = _restaurant.asStateFlow()

    private val _platos = MutableStateFlow<List<Plato>>(emptyList())
    val platos: StateFlow<List<Plato>> = _platos.asStateFlow()


    private val _reviews = MutableStateFlow<List<Review>>(emptyList())  // ✅ Nuevo StateFlow
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()       // ✅ Nuevo StateFlow

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRestaurantDetail(restaurantId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val restaurantData = foodRepository.getRestaurantById(restaurantId)
            _restaurant.value = restaurantData

            // ✅ Cargar platos desde Firestore
            if (restaurantData?.uid?.isNotEmpty() == true) {
                platoRepository.getPlatos(restaurantData.uid).collectLatest { platos ->
                    _platos.value = platos
                }
                // ✅ Cargar reseñas en tiempo real
                reviewRepository.getReviewsForRestaurant(restaurantData.uid).collectLatest { reviews ->
                    _reviews.value = reviews
                    _isLoading.value = false
                }
            } else {
                _isLoading.value = false
            }
        }
    }
}