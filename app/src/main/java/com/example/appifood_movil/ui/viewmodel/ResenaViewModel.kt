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

data class Resena(
    val id: String = "",
    val usuario: String = "",
    val comentario: String = "",
    val calificacion: Int = 5,
    val respuesta: String = "",
    val fecha: Long = System.currentTimeMillis()
)

@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadResenas(restauranteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("restaurants")
                    .document(restauranteId)
                    .collection("resenas")
                    .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val resenas = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Resena::class.java)?.copy(id = doc.id)
                }
                _resenas.value = resenas
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun responderResena(restauranteId: String, resenaId: String, respuesta: String) {
        viewModelScope.launch {
            try {
                firestore.collection("restaurants")
                    .document(restauranteId)
                    .collection("resenas")
                    .document(resenaId)
                    .update("respuesta", respuesta)
                    .await()

                // Actualizar local
                val current = _resenas.value
                val updated = current.map {
                    if (it.id == resenaId) it.copy(respuesta = respuesta) else it
                }
                _resenas.value = updated
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}