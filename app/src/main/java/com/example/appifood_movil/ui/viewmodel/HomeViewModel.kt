package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.domain.model.Restaurant
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        // Carga inicial de restaurantes desde el repositorio (que ahora es un Flow)
        viewModelScope.launch {
            foodRepository.getRestaurants().collectLatest { list ->
                searchResults = list
            }
        }
    }

    fun onSearchChange(newText: String) {
        searchText = newText
        // En una app real, aquí filtraríamos los resultados o llamaríamos a la API
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
