// ui/viewmodel/ProductDetailViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.model.Adicion
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

    private val _product    = MutableStateFlow<FoodProduct?>(null)
    val product: StateFlow<FoodProduct?> = _product

    private val _isLoading  = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _adiciones   = MutableStateFlow<List<Adicion>>(emptyList())
    val adiciones: StateFlow<List<Adicion>> = _adiciones

    fun loadProduct(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("ProductDetail", "🔍 Buscando producto con ID: $id")

            // ── Intentar Firestore primero ────────────────────────
            // Los platos del restaurante tienen id >= 100 (restaurantId*100 + index)
            // Los del FakeData tienen id < 100
            val product = if (id >= 100) {
                android.util.Log.d("ProductDetail", "📡 Buscando en Firestore (id=$id)")
                repository.getProductFromFirestore(id)
                    ?: repository.getProductById(id)   // fallback a FakeData
            } else {
                android.util.Log.d("ProductDetail", "📦 Buscando en FakeData (id=$id)")
                repository.getProductById(id)
            }

            android.util.Log.d("ProductDetail",
                "✅ Producto: ${product?.name}, " +
                        "Precio: ${product?.price}, " +
                        "Adiciones: ${product?.adiciones?.size ?: 0}, " +
                        "ImagenUrl: ${product?.imagenUrl}")

            _product.value      = product
            _description.value  = product?.description
                ?: "Delicioso plato preparado con los mejores ingredientes."
            _adiciones.value    = product?.adiciones ?: emptyList()
            _isLoading.value    = false
        }
    }
}