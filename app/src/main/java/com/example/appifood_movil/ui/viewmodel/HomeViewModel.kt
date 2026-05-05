package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.data.restaurants
import com.example.appifood_movil.data.searchRestaurants
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.data.allProducts
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("Todas")
        private set

    var filter by mutableStateOf(HomeFilter())
        private set

    var cartCount by mutableStateOf(0)
        private set

    var searchResults by mutableStateOf(restaurants)
        private set

    fun onSearchChange(newText: String) {
        searchText = newText
        searchResults = if (newText.length > 2) {
            searchRestaurants(newText)
        } else {
            restaurants
        }
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
    }

    val filteredProducts: List<FoodProduct>
        get() = if (selectedCategory == "Todos" || selectedCategory == "Todas") {
            allProducts
        } else {
            allProducts.filter { it.category == selectedCategory }
        }
}
