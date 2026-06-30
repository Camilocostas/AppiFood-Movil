// ui/viewmodel/HomeViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.domain.model.FoodProduct
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    private val _filteredProducts = MutableStateFlow<List<FoodProduct>>(emptyList())
    val filteredProducts: StateFlow<List<FoodProduct>> = _filteredProducts

    private val _promotionProducts = MutableStateFlow<List<FoodProduct>>(emptyList())
    val promotionProducts: StateFlow<List<FoodProduct>> = _promotionProducts

    // ✅ Usar StateFlow para selectedCategory
    private val _selectedCategory = MutableStateFlow("Todos")  // 🔑 Clave
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _filteredPromotions = MutableStateFlow<List<FoodProduct>>(emptyList())
    val filteredPromotions: StateFlow<List<FoodProduct>> = _filteredPromotions

    var searchText by mutableStateOf("")  // Este sí puede ir con mutableStateOf porque no es Flow, pero mejor usa StateFlow
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
                _restaurants.value = list
                loadFilteredProducts()
            }
        }
    }

    fun onSearchChange(newText: String) {
        searchText = newText
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category  // ✅ Actualizar StateFlow
        viewModelScope.launch {
            loadFilteredProducts()
            updateFilteredPromotions()  // ✅ Actualizar promociones filtradas
        }
    }

    // 🔧 Cargar productos filtrados y promociones
    private suspend fun loadFilteredProducts() {
        try {
            val allProducts = foodRepository.getProducts()
            Log.d("HomeViewModel", "Productos totales: ${allProducts.size}")

            val category = _selectedCategory.value

            // Productos filtrados por categoría
            _filteredProducts.value = if (category == "Todos" || category == "Todas") {
                allProducts
            } else {
                allProducts.filter { it.categoria == category }
            }

            // Productos en promoción (todos)
            _promotionProducts.value = allProducts.filter { it.precioPromocion > 0 }

            // ✅ Actualizar promociones filtradas según categoría
            updateFilteredPromotions()

            Log.d("HomeViewModel", "Productos filtrados: ${_filteredProducts.value.size}")
            Log.d("HomeViewModel", "Productos en promoción: ${_promotionProducts.value.size}")
            Log.d("HomeViewModel", "Promociones filtradas: ${_filteredPromotions.value.size}")
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error cargando productos: ${e.message}")
            _filteredProducts.value = emptyList()
            _promotionProducts.value = emptyList()
            _filteredPromotions.value = emptyList()
        }
    }

    // 🔧 Actualizar promociones según categoría
    private fun updateFilteredPromotions() {
        val category = _selectedCategory.value
        _filteredPromotions.value = if (category == "Todos" || category == "Todas") {
            _promotionProducts.value
        } else {
            _promotionProducts.value.filter { it.categoria == category }
        }
    }
}