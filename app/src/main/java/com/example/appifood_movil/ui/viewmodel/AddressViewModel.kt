package com.example.appifood_movil.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.request.AddressRequest
import com.example.appifood_movil.data.local.TokenManager
import com.example.appifood_movil.domain.model.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        val token = tokenManager.getBearerToken() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getAddresses(token)
                if (response.isSuccessful && response.body() != null) {
                    _addresses.value = response.body()!!.data.map { dto ->
                        Address(
                            id = dto.id,
                            title = dto.title,
                            address = dto.address,
                            details = dto.details,
                            latitude = dto.latitude,
                            longitude = dto.longitude
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AddressVM", "Error loading addresses", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addAddress(title: String, address: String, details: String?) {
        val token = tokenManager.getBearerToken() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = AddressRequest(title, address, details)
                val response = apiService.createAddress(token, request)
                if (response.isSuccessful) {
                    loadAddresses()
                }
            } catch (e: Exception) {
                Log.e("AddressVM", "Error adding address", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAddress(addressId: Int) {
        val token = tokenManager.getBearerToken() ?: return
        viewModelScope.launch {
            try {
                val response = apiService.deleteAddress(token, addressId)
                if (response.isSuccessful) {
                    _addresses.value = _addresses.value.filter { it.id != addressId }
                }
            } catch (e: Exception) {
                Log.e("AddressVM", "Error deleting address", e)
            }
        }
    }
}
