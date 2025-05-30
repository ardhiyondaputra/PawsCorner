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

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object EmailVerificationSent : AuthState()
    object EmailNotVerified : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            _authState.value = AuthState.Success
        } else {
            _authState.value = AuthState.Idle
        }
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
                val user = result.user

                if (user != null) {
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

                    // Save user data to Firestore (but mark as unverified)
                    val userData = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "phone" to phone,
                        "emailVerified" to false,
                        "createdAt" to System.currentTimeMillis()
                    )

                    firestore.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .await()

                    _authState.value = AuthState.EmailVerificationSent
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
                val user = auth.currentUser
                if (user != null) {
                    // Reload user to get latest email verification status
                    user.reload().await()

                    if (user.isEmailVerified) {
                        // Update user data in Firestore
                        firestore.collection("users")
                            .document(user.uid)
                            .update("emailVerified", true)
                            .await()

                        _authState.value = AuthState.Success
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

                val user = auth.currentUser
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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    if (user.isEmailVerified) {
                        _authState.value = AuthState.Success
                        Log.d(TAG, "Login successful")
                    } else {
                        // Sign out user if email not verified
                        auth.signOut()
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
        _authState.value = AuthState.Idle
        Log.d(TAG, "User logged out")
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}