package kelompok4.uasmobile2.pawscorner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kelompok4.uasmobile2.pawscorner.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    // State untuk mengamati status login
    val isLoggedIn = userPreferences.isLoggedIn
        .map { it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    // Tambahkan ini:
    val userName = userPreferences.userName
    val userEmail = userPreferences.userEmail

    // Fungsi untuk menyimpan status login
    fun login() {
        viewModelScope.launch {
            userPreferences.setLoggedIn(true)
        }
    }

    // Fungsi untuk logout
    fun logout() {
        viewModelScope.launch {
            userPreferences.setLoggedIn(false)
        }
    }
}
