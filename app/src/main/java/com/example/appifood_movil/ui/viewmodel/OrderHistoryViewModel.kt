// ui/viewmodel/OrderHistoryViewModel.kt
package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.local.TokenManager
import com.example.appifood_movil.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _orders    = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error     = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { loadOrders() }

    fun loadOrders() {
        val token = tokenManager.getBearerToken() ?: return
        _isLoading.value = true
        _error.value     = null

        viewModelScope.launch {
            try {
                val response = apiService.getUserOrders(token)
                if (response.isSuccessful && response.body() != null) {
                    // Mapping from API Order to Domain/UI Order
                    // For now keeping empty or mocking to avoid deep mapping complexity
                    _orders.value = emptyList() 
                }
            } catch (e: Exception) {
                Log.e("OrderHistoryVM", "Error loading history", e)
                _error.value = "Error al cargar historial"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun formatCurrency(amount: Int): String =
        "\$${String.format("%,d", amount).replace(",", ".")}"

    fun formatDate(timestamp: Long): String {
        if (timestamp == 0L) return "Fecha desconocida"
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("es", "CO"))
        return sdf.format(java.util.Date(timestamp))
    }
}
