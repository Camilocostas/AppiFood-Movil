package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.domain.model.FoodProduct
import com.example.appifood_movil.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _product = MutableStateFlow<FoodProduct?>(null)
    val product: StateFlow<FoodProduct?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ── ESTADO PARA LA DESCRIPCIÓN ──────────────────────────────
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    fun loadProduct(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val product = repository.getProductById(id)
            _product.value = product
            _description.value = product?.description ?: "Delicioso plato preparado con los mejores ingredientes para satisfacer tu paladar."
            _isLoading.value = false
        }
    }
}