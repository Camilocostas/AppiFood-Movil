package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("Todas")
        private set

    var filter by mutableStateOf(HomeFilter())
        private set

    var cartCount by mutableStateOf(0)
        private set

    var searchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    init {
        // Carga inicial
        searchResults = foodRepository.searchRestaurants("")
    }

    fun onSearchChange(newText: String) {
        searchText = newText
        searchResults = if (newText.length > 2) {
            foodRepository.searchRestaurants(newText)
        } else {
            foodRepository.searchRestaurants("")
        }
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
    }

    val filteredProducts: List<FoodProduct>
        get() {
            val allProducts = foodRepository.getProducts()
            return if (selectedCategory == "Todos" || selectedCategory == "Todas") {
                allProducts
            } else {
                allProducts.filter { it.category == selectedCategory }
            }
        }
}
