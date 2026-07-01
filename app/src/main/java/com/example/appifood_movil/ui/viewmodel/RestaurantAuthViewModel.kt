package com.example.appifood_movil.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantAuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun loginRestaurant(email: String, password: String) {
        // Bridge to Railway or mock
        _error.value = "Módulo de restaurante en desarrollo para Railway"
    }

    fun registerRestaurant(
        ownerName: String,
        restaurantName: String,
        email: String,
        phone: String,
        documentNumber: String,
        password: String
    ) {
        // Bridge to Railway or mock
        _error.value = "Módulo de restaurante en desarrollo para Railway"
    }

    suspend fun uploadProductImage(
        restauranteId: String,
        productId: String,
        imageUri: Uri
    ): Result<String> {
        return Result.failure(Exception("Cloudinary integration required"))
    }
}
