package com.example.appifood_movil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RestaurantDashboardViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _platosActivos = MutableStateFlow(0)
    val platosActivos: StateFlow<Int> = _platosActivos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPlatosActivos(restauranteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("restaurants")
                    .document(restauranteId)
                    .get()
                    .await()

                if (doc.exists()) {
                    val dishes = doc.get("dishes") as? List<Map<String, Any>> ?: emptyList()
                    val activos = dishes.count {
                        it["disponible"] as? Boolean ?: true
                    }
                    _platosActivos.value = activos
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPlatosActivosCount(): Int {
        return _platosActivos.value
    }
}