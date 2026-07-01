package com.example.appifood_movil.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appifood_movil.data.api.ApiService
import com.example.appifood_movil.data.api.request.LoginRequest
import com.example.appifood_movil.data.api.request.RegisterRequest
import com.example.appifood_movil.data.local.TokenManager
import com.example.appifood_movil.data.model.PaymentMethod
import com.example.appifood_movil.data.model.UserData
import com.example.appifood_movil.data.model.MockFirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(tokenManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _user = MutableStateFlow<MockFirebaseUser?>(null)
    val user: StateFlow<MockFirebaseUser?> = _user.asStateFlow()

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()

    init {
        if (tokenManager.isLoggedIn()) {
            getMe()
        }
    }

    private var onAuthSuccessCallback: (() -> Unit)? = null

    fun setOnAuthSuccess(callback: () -> Unit) {
        this.onAuthSuccessCallback = callback
    }

    fun initGoogleSignIn(context: Context, webClientId: String) {}
    fun getGoogleSignInIntent(): Intent = Intent()
    fun signInWithGoogle(idToken: String) { _error.value = "Google login disabled" }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.data != null) {
                    val authData = response.body()!!.data!!
                    tokenManager.saveToken(authData.token)
                    
                    val emailVal = authData.user?.email ?: ""
                    _userData.value = UserData(
                        names = authData.user?.name ?: "",
                        email = emailVal
                    )
                    
                    _isLoggedIn.value = true
                    _user.value = MockFirebaseUser(uid = "0", email = emailVal)
                    onSuccess()
                    onAuthSuccessCallback?.invoke()
                } else {
                    _error.value = "Error: Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
                Log.e("AuthVM", "Login error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerWithEmail(
        names: String,
        lastNames: String,
        phone: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = RegisterRequest(
                    name = "$names $lastNames",
                    email = email,
                    password = password,
                    password_confirmation = password
                )
                val response = apiService.register(request)
                if (response.isSuccessful && response.body()?.data != null) {
                    val authData = response.body()!!.data!!
                    tokenManager.saveToken(authData.token)
                    
                    val emailVal = authData.user?.email ?: ""
                    _userData.value = UserData(
                        names = authData.user?.name ?: "",
                        lastNames = lastNames,
                        phone = phone,
                        email = emailVal
                    )
                    
                    _isLoggedIn.value = true
                    _user.value = MockFirebaseUser(uid = "0", email = emailVal)
                    onSuccess()
                } else {
                    _error.value = "Error en el registro"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
                Log.e("AuthVM", "Register error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        tokenManager.clearToken()
        _isLoggedIn.value = false
        _userData.value = null
        _user.value = null
    }

    fun clearError() { _error.value = null }

    fun getMe() {
        val token = tokenManager.getBearerToken() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMe(token)
                if (response.isSuccessful && response.body() != null) {
                    val userDto = response.body()!!
                    _userData.value = UserData(names = userDto.name, email = userDto.email)
                    _user.value = MockFirebaseUser(uid = "0", email = userDto.email)
                }
            } catch (e: Exception) {
                Log.e("AuthVM", "Error fetching user data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserDataFromFirestore(uid: String) { getMe() }
    
    suspend fun uploadProfileImage(uid: String, imageUri: Uri): String? {
        Log.d("AuthVM", "Upload profile image (Mock)")
        return "mock_url"
    }

    fun updateUserDataInFirestore(uid: String, userData: UserData, onComplete: (Boolean) -> Unit) {
        onComplete(true)
    }

    suspend fun deleteProfileImage(uid: String): Boolean {
        Log.d("AuthVM", "Delete profile image (Mock)")
        return true
    }

    fun loadPaymentMethods(uid: String) {}
    fun addPaymentMethod(uid: String, paymentMethod: PaymentMethod, onComplete: (Boolean) -> Unit) { onComplete(true) }
    fun removePaymentMethod(uid: String, methodId: String, onComplete: (Boolean) -> Unit) { onComplete(true) }
    fun setDefaultPaymentMethod(uid: String, methodId: String, onComplete: (Boolean) -> Unit) { onComplete(true) }
}
