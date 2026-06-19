package com.example.appifood_movil.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private lateinit var googleSignInClient: GoogleSignInClient
    private var onAuthSuccessCallback: (() -> Unit)? = null

    fun setOnAuthSuccess(callback: () -> Unit) {
        this.onAuthSuccessCallback = callback
    }

    fun initGoogleSignIn(context: Context, webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun signInWithGoogle(idToken: String) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _error.value = null
                    onAuthSuccessCallback?.invoke()
                } else {
                    _error.value = task.exception?.message ?: "Error al iniciar sesión"
                }
            }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _error.value = null
                    onSuccess()
                } else {
                    _error.value = task.exception?.message ?: "Error de autenticación"
                }
            }
    }

    // ⭐ NUEVO: Crear cuenta con email y contraseña
    fun createUserWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _error.value = null
                    android.util.Log.d("AuthViewModel", "✅ Cuenta creada: ${auth.currentUser?.email}")
                    onSuccess()
                } else {
                    _error.value = task.exception?.message ?: "Error al crear cuenta"
                    android.util.Log.e("AuthViewModel", "❌ Error: ${task.exception?.message}")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        if (::googleSignInClient.isInitialized) {
            googleSignInClient.signOut()
        }
        _user.value = null
    }

    fun clearError() {
        _error.value = null
    }
}