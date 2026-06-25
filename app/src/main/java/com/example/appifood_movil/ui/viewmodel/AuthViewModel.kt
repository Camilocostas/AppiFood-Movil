package com.example.appifood_movil.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.appifood_movil.data.model.PaymentMethod
import com.example.appifood_movil.data.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

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

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
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
                    _error.value = task.exception?.message ?: "Error al iniciar sesión con Google"
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

    fun registerWithEmail(
        names: String,
        lastNames: String,
        phone: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        _error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    firebaseUser?.let { user ->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$names $lastNames")
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnSuccessListener {
                                saveUserDataToFirestore(
                                    uid = user.uid,
                                    names = names,
                                    lastNames = lastNames,
                                    phone = phone,
                                    email = email
                                ) { success ->
                                    if (success) {
                                        _user.value = user
                                        _error.value = null
                                        onSuccess()
                                    } else {
                                        _error.value = "Cuenta creada pero error al guardar datos"
                                        _user.value = user
                                        onSuccess()
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                _error.value = "Error al actualizar perfil: ${e.message}"
                                _isLoading.value = false
                            }
                    } ?: run {
                        _error.value = "Error: usuario nulo después de registro"
                        _isLoading.value = false
                    }
                } else {
                    _error.value = when {
                        task.exception?.message?.contains("email already in use") == true ->
                            "Este correo electrónico ya está registrado"
                        task.exception?.message?.contains("password is weak") == true ->
                            "La contraseña debe tener al menos 6 caracteres"
                        task.exception?.message?.contains("invalid email") == true ->
                            "El formato del correo electrónico no es válido"
                        else -> task.exception?.message ?: "Error al crear cuenta"
                    }
                    _isLoading.value = false
                }
            }
    }

    private fun saveUserDataToFirestore(
        uid: String,
        names: String,
        lastNames: String,
        phone: String,
        email: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userData = UserData(
            names = names,
            lastNames = lastNames,
            phone = phone,
            email = email,
            createdAt = System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Datos guardados en Firestore exitosamente")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error guardando en Firestore: ${e.message}")
                onComplete(false)
            }
    }

    fun createUserWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _error.value = null
                    onSuccess()
                } else {
                    _error.value = task.exception?.message ?: "Error al crear cuenta"
                }
            }
    }

    fun getUserDataFromFirestore(uid: String) {
        _isLoading.value = true
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.toObject(UserData::class.java)
                    _userData.value = data
                } else {
                    _userData.value = null
                }
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error obteniendo datos: ${e.message}")
                _userData.value = null
                _isLoading.value = false
            }
    }

    fun updateUserDataInFirestore(
        uid: String,
        userData: UserData,
        onComplete: (Boolean) -> Unit
    ) {
        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error actualizando datos: ${e.message}")
                onComplete(false)
            }
    }

    // ── MÉTODOS PARA PAGOS ──────────────────────────────────────────

    fun loadPaymentMethods(uid: String) {
        _isLoading.value = true
        firestore.collection("users")
            .document(uid)
            .collection("paymentMethods")
            .get()
            .addOnSuccessListener { documents ->
                val methods = mutableListOf<PaymentMethod>()
                for (document in documents) {
                    val method = document.toObject(PaymentMethod::class.java)
                    methods.add(method.copy(id = document.id))
                }
                _paymentMethods.value = methods
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error cargando métodos de pago: ${e.message}")
                _isLoading.value = false
            }
    }

    fun addPaymentMethod(uid: String, paymentMethod: PaymentMethod, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        val methodsRef = firestore.collection("users")
            .document(uid)
            .collection("paymentMethods")

        // Si es el primer método o es predeterminado, actualizar otros
        if (paymentMethod.isDefault) {
            // Quitar default de otros métodos
            methodsRef.get()
                .addOnSuccessListener { documents ->
                    val batch = firestore.batch()
                    for (document in documents) {
                        batch.update(document.reference, "isDefault", false)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            // Agregar nuevo método
                            addNewPaymentMethod(methodsRef, paymentMethod, onComplete)
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            onComplete(false)
                        }
                }
                .addOnFailureListener { e ->
                    _isLoading.value = false
                    onComplete(false)
                }
        } else {
            // Agregar sin ser default
            addNewPaymentMethod(methodsRef, paymentMethod, onComplete)
        }
    }

    private fun addNewPaymentMethod(
        methodsRef: com.google.firebase.firestore.CollectionReference,
        paymentMethod: PaymentMethod,
        onComplete: (Boolean) -> Unit
    ) {
        val docRef = methodsRef.document()
        val methodWithId = paymentMethod.copy(id = docRef.id)

        docRef.set(methodWithId)
            .addOnSuccessListener {
                // Actualizar lista local
                val currentList = _paymentMethods.value.toMutableList()
                currentList.add(methodWithId)
                _paymentMethods.value = currentList
                _isLoading.value = false
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error agregando método de pago: ${e.message}")
                _isLoading.value = false
                onComplete(false)
            }
    }

    fun removePaymentMethod(uid: String, methodId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        firestore.collection("users")
            .document(uid)
            .collection("paymentMethods")
            .document(methodId)
            .delete()
            .addOnSuccessListener {
                // Actualizar lista local
                val currentList = _paymentMethods.value.toMutableList()
                currentList.removeAll { it.id == methodId }
                _paymentMethods.value = currentList
                _isLoading.value = false
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error eliminando método de pago: ${e.message}")
                _isLoading.value = false
                onComplete(false)
            }
    }

    fun setDefaultPaymentMethod(uid: String, methodId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        val methodsRef = firestore.collection("users")
            .document(uid)
            .collection("paymentMethods")

        // Quitar default de todos
        methodsRef.get()
            .addOnSuccessListener { documents ->
                val batch = firestore.batch()
                for (document in documents) {
                    batch.update(document.reference, "isDefault", false)
                }
                // Establecer el nuevo default
                batch.update(methodsRef.document(methodId), "isDefault", true)

                batch.commit()
                    .addOnSuccessListener {
                        // Actualizar lista local
                        val currentList = _paymentMethods.value.map { method ->
                            method.copy(isDefault = method.id == methodId)
                        }
                        _paymentMethods.value = currentList
                        _isLoading.value = false
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("AuthViewModel", "Error estableciendo método default: ${e.message}")
                        _isLoading.value = false
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                onComplete(false)
            }
    }

    fun signOut() {
        auth.signOut()
        if (::googleSignInClient.isInitialized) {
            googleSignInClient.signOut()
        }
        _user.value = null
        _paymentMethods.value = emptyList()
    }

    fun clearError() {
        _error.value = null
    }
}