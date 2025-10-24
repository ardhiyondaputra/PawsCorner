package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.ui.components.CustomInputField
import kelompok4.uasmobile2.pawscorner.ui.components.CustomPopup
import kelompok4.uasmobile2.pawscorner.ui.components.PrimaryButton
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val userData by authViewModel.userData.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf(userData?.username ?: "") }
    var phone by remember { mutableStateOf(userData?.phone ?: "") }

    // State untuk popup dan loading
    var showSuccessPopup by remember { mutableStateOf(false) }
    var showErrorPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Update state saat userData berubah
    LaunchedEffect(userData) {
        username = userData?.username ?: ""
        phone = userData?.phone ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(35.dp))

        // ========== HEADER ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 35.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = "Edit Profil",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ========== CUSTOM INPUT FIELD - USERNAME ==========
        CustomInputField(
            value = username,
            onValueChange = { username = it },
            placeholder = "Nama Pengguna",
            leadingIconVector = Icons.Default.Person, // Material Icon
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ========== CUSTOM INPUT FIELD - PHONE ==========
        CustomInputField(
            value = phone,
            onValueChange = {
                // Validasi hanya angka
                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                    phone = it
                }
            },
            placeholder = "Nomor Telepon",
            leadingIconVector = Icons.Default.Phone, // Material Icon
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ========== PRIMARY BUTTON ==========
        PrimaryButton(
            text = "Update Profile",
            onClick = {
                // Validasi input
                when {
                    username.isBlank() -> {
                        errorMessage = "Nama pengguna tidak boleh kosong"
                        showErrorPopup = true
                    }
                    username.length < 3 -> {
                        errorMessage = "Nama pengguna minimal 3 karakter"
                        showErrorPopup = true
                    }
                    phone.isBlank() -> {
                        errorMessage = "Nomor telepon tidak boleh kosong"
                        showErrorPopup = true
                    }
                    phone.length < 10 -> {
                        errorMessage = "Nomor telepon minimal 10 digit"
                        showErrorPopup = true
                    }
                    else -> {
                        // Update profile
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                authViewModel.updateUserProfile(username, phone)
                                delay(500) // Simulasi delay untuk smooth UX
                                isLoading = false
                                showSuccessPopup = true
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "Gagal update profil: ${e.message}"
                                showErrorPopup = true
                            }
                        }
                    }
                }
            },
            enabled = !isLoading,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Info helper text
        if (!isLoading) {
            Text(
                text = "Pastikan data yang Anda masukkan sudah benar",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

    // ========== POP-UP SUCCESS ==========
    if (showSuccessPopup) {
        CustomPopup(
            title = "Berhasil!",
            message = "Profil Anda berhasil diperbarui",
            confirmText = "OK",
            onDismiss = {
                showSuccessPopup = false
                navController.popBackStack()
            },
            onConfirm = {
                showSuccessPopup = false
                navController.popBackStack()
            },
            isError = false
        )

        // Auto dismiss dan kembali setelah 2 detik
        LaunchedEffect(Unit) {
            delay(2000)
            showSuccessPopup = false
            navController.popBackStack()
        }
    }

    // ========== POP-UP ERROR ==========
    if (showErrorPopup) {
        CustomPopup(
            title = "Gagal",
            message = errorMessage,
            confirmText = "OK",
            onDismiss = { showErrorPopup = false },
            isError = true
        )
    }

}