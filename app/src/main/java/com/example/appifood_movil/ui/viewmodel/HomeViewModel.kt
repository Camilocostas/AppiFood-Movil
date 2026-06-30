// ui/viewmodel/HomeViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.domain.model.FoodProduct
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    private val _filteredProducts = MutableStateFlow<List<FoodProduct>>(emptyList())
    val filteredProducts: StateFlow<List<FoodProduct>> = _filteredProducts

    private val _promotionProducts = MutableStateFlow<List<FoodProduct>>(emptyList())
    val promotionProducts: StateFlow<List<FoodProduct>> = _promotionProducts  // ✅ Nuevo

    var searchText by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("Todas")
        private set

    var filter by mutableStateOf(HomeFilter())
        private set

    var cartCount by mutableStateOf(0)
        private set

    init {
        loadRestaurants()
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            foodRepository.getRestaurants().collectLatest { list ->
                Log.d("HomeViewModel", "✅ Restaurantes cargados: ${list.size}")
                list.forEach { restaurant ->
                    Log.d("HomeViewModel", "  - ${restaurant.name} (ID: ${restaurant.id})")
                }
                _restaurants.value = list
                loadFilteredProducts()
            }
        }
    }

    fun onSearchChange(newText: String) {
        searchText = newText
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
        viewModelScope.launch {
            loadFilteredProducts()
        }
    }

    private suspend fun loadFilteredProducts() {
        try {
            val allProducts = foodRepository.getProducts()
            Log.d("HomeViewModel", "Productos totales: ${allProducts.size}")

            // ✅ Productos filtrados por categoría (para la sección normal)
            _filteredProducts.value = if (selectedCategory == "Todos" || selectedCategory == "Todas") {
                allProducts
            } else {
                allProducts.filter { it.category == selectedCategory }
            }

            // ✅ Productos en promoción (para "Promociones de Hoy")
            _promotionProducts.value = allProducts.filter { it.precioPromocion > 0 }

            Log.d("HomeViewModel", "Productos filtrados: ${_filteredProducts.value.size}")
            Log.d("HomeViewModel", "Productos en promoción: ${_promotionProducts.value.size}")
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error cargando productos: ${e.message}")
            _filteredProducts.value = emptyList()
            _promotionProducts.value = emptyList()
        }
    }
}