package com.example.appifood_movil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.HomeFilter
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.data.model.sampleRestaurants
import com.example.appifood_movil.data.model.searchRestaurants

class HomeViewModel : ViewModel() {
    var selectedCategory by mutableStateOf("Hamburguesas")
        private set

    var searchText by mutableStateOf("")
        private set

    var searchResults by mutableStateOf(sampleRestaurants)
        private set

    var cartCount by mutableStateOf(0)
        private set

    var filter by mutableStateOf(HomeFilter())
        private set

    fun onCategorySelected(category: String) {
        selectedCategory = category
    }

    fun onSearchChange(text: String) {
        searchText = text
        searchResults = searchRestaurants(text)
    }

    fun onAddToCart() {
        cartCount++
    }

    fun onApplyFilter(newFilter: HomeFilter) {
        filter = newFilter
    }
}
