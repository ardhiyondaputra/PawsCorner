package kelompok4.uasmobile2.pawscorner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val user = auth.currentUser
        if (user != null) {
            _userEmail.value = user.email ?: ""
            val uid = user.uid

            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    _errorMessage.value = null

                    // Gunakan await() untuk menunggu hasil dari Firestore
                    val document = firestore.collection("users")
                        .document(uid)
                        .get()
                        .await()

                    if (document.exists()) {
                        _userName.value = document.getString("username") ?: ""
                        _userPhone.value = document.getString("phone") ?: ""
                        Log.d(TAG, "Data berhasil diambil: username=${_userName.value}, phone=${_userPhone.value}")
                    } else {
                        Log.w(TAG, "Document tidak ditemukan untuk UID: $uid")
                        _errorMessage.value = "Data user tidak ditemukan"
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching user data", e)
                    _errorMessage.value = "Gagal mengambil data user: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            Log.w(TAG, "User belum login")
            _errorMessage.value = "User belum login"
        }
    }

    // Fungsi untuk refresh data
    fun refreshUserData() {
        fetchUserData()
    }

    // Fungsi untuk update profil user
    fun updateUserProfile(username: String, phone: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true

                    val updates = hashMapOf<String, Any>(
                        "username" to username,
                        "phone" to phone,
                        "updatedAt" to System.currentTimeMillis()
                    )

                    firestore.collection("users")
                        .document(user.uid)
                        .update(updates)
                        .await()

                    // Update StateFlow setelah berhasil update
                    _userName.value = username
                    _userPhone.value = phone

                    onSuccess()
                    Log.d(TAG, "Profil berhasil diupdate")

                } catch (e: Exception) {
                    Log.e(TAG, "Error updating profile", e)
                    onError("Gagal update profil: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            onError("User belum login")
        }
    }

    // Fungsi untuk clear error message
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}