package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.data.restaurants
import com.example.appifood_movil.data.searchRestaurants

class HomeViewModel : ViewModel() {

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
        searchResults = if (category == "Todas") {
            restaurants
        } else {
            restaurants.filter { it.category == category }
        }
    }

    fun onApplyFilter(newFilter: HomeFilter) {
        filter = newFilter
    }

    fun onAddToCart() {
        cartCount++
    }
}