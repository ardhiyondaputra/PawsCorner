package kelompok4.uasmobile2.pawscorner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kelompok4.uasmobile2.pawscorner.data.UserData

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object EmailVerificationSent : AuthState()
    object EmailNotVerified : AuthState()
    object PasswordResetEmailSent : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val currentUser = auth.currentUser
        _currentUser.value = currentUser

        if (currentUser != null && currentUser.isEmailVerified) {
            _authState.value = AuthState.Success
            loadUserData(currentUser)
        } else {
            _authState.value = AuthState.Idle
            _userData.value = null
        }
    }

    // Fungsi untuk mendapatkan FirebaseUser saat ini
    fun getCurrentFirebaseUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Fungsi untuk memuat data user dari Firestore
    private fun loadUserData(user: FirebaseUser) {
        viewModelScope.launch {
            try {
                val document = firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                if (document.exists()) {
                    val userData = UserData(
                        uid = user.uid,
                        username = document.getString("username") ?: "",
                        email = document.getString("email") ?: user.email ?: "",
                        phone = document.getString("phone") ?: "",
                        emailVerified = document.getBoolean("emailVerified") ?: false
                    )
                    _userData.value = userData
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data", e)
            }
        }
    }

    // Fungsi untuk mendapatkan data user
    fun getUserData(): UserData? {
        return _userData.value
    }

    fun registerWithEmailVerification(
        email: String,
        password: String,
        username: String,
        phone: String
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Create user with email and password
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user: FirebaseUser? = result.user

                if (user != null) {
                    _currentUser.value = user

                    // Create action code settings for email verification
                    val actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl("https://pawscorner.page.link/verify") // Replace with your domain
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                            "kelompok4.uasmobile2.pawscorner",
                            true, // installIfNotAvailable
                            null // minimumVersion
                        )
                        .build()

                    // Send email verification
                    user.sendEmailVerification(actionCodeSettings).await()

                    // Save user data to Firestore dengan UID sebagai Document ID
                    val userData = hashMapOf(
                        "username" to username,        // Sesuai dengan AuthViewModel
                        "email" to email,
                        "phone" to phone,
                        "emailVerified" to false,
                        "createdAt" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                        // JANGAN simpan password di Firestore!
                    )

                    // Gunakan user.uid sebagai Document ID
                    firestore.collection("users")
                        .document(user.uid)  // INI YANG PENTING: gunakan UID sebagai Document ID
                        .set(userData)
                        .await()

                    _authState.value = AuthState.EmailVerificationSent
                    Log.d(TAG, "User registered with UID: ${user.uid}")
                    Log.d(TAG, "Email verification sent to: $email")

                } else {
                    _authState.value = AuthState.Error("Gagal membuat akun")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Registration error", e)
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("email address is already in use") == true ->
                            "Email sudah terdaftar"
                        e.message?.contains("weak password") == true ->
                            "Password terlalu lemah (minimal 6 karakter)"
                        e.message?.contains("invalid email") == true ->
                            "Format email tidak valid"
                        else -> e.message ?: "Terjadi kesalahan saat registrasi"
                    }
                )
            }
        }
    }

    fun checkEmailVerification() {
        viewModelScope.launch {
            try {
                val user: FirebaseUser? = auth.currentUser
                if (user != null) {
                    // Reload user to get latest email verification status
                    user.reload().await()

                    if (user.isEmailVerified) {
                        _currentUser.value = user

                        // Update user data in Firestore
                        firestore.collection("users")
                            .document(user.uid)
                            .update("emailVerified", true)
                            .await()

                        _authState.value = AuthState.Success
                        loadUserData(user)
                        Log.d(TAG, "Email verified successfully")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking email verification", e)
                // Don't change state on error, keep checking
            }
        }
    }

    fun resendEmailVerification() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val user: FirebaseUser? = auth.currentUser
                if (user != null) {
                    val actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl("https://pawscorner.page.link/verify") // Replace with your domain
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                            "kelompok4.uasmobile2.pawscorner",
                            true,
                            null
                        )
                        .build()

                    user.sendEmailVerification(actionCodeSettings).await()
                    _authState.value = AuthState.EmailVerificationSent
                    Log.d(TAG, "Email verification resent")
                } else {
                    _authState.value = AuthState.Error("User tidak ditemukan")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error resending email verification", e)
                _authState.value = AuthState.Error("Gagal mengirim ulang email verifikasi")
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetEmailSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Gagal mengirim email reset")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user: FirebaseUser? = result.user

                if (user != null) {
                    _currentUser.value = user

                    if (user.isEmailVerified) {
                        _authState.value = AuthState.Success
                        loadUserData(user)
                        Log.d(TAG, "Login successful")
                    } else {
                        // Sign out user if email not verified
                        auth.signOut()
                        _currentUser.value = null
                        _userData.value = null
                        _authState.value = AuthState.EmailNotVerified
                    }
                } else {
                    _authState.value = AuthState.Error("Login gagal")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("invalid email") == true -> "Format email tidak valid"
                        e.message?.contains("user not found") == true -> "Email tidak terdaftar"
                        e.message?.contains("wrong password") == true -> "Password salah"
                        e.message?.contains("invalid credential") == true -> "Email atau password salah"
                        else -> e.message ?: "Terjadi kesalahan saat login"
                    }
                )
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _userData.value = null
        _authState.value = AuthState.Idle
        Log.d(TAG, "User logged out")
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    // Fungsi untuk update profil user
    fun updateUserProfile(username: String, phone: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null) {
                    val updates = hashMapOf<String, Any>(
                        "username" to username,
                        "phone" to phone,
                        "updatedAt" to System.currentTimeMillis()
                    )

                    firestore.collection("users")
                        .document(user.uid)
                        .update(updates)
                        .await()

                    // Reload user data
                    loadUserData(user)
                    Log.d(TAG, "User profile updated successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating user profile", e)
            }
        }
    }
}