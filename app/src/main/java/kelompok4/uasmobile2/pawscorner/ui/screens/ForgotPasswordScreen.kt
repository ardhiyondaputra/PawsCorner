package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kelompok4.uasmobile2.pawscorner.ui.components.CustomInputField
import kelompok4.uasmobile2.pawscorner.ui.components.CustomPopup
import kelompok4.uasmobile2.pawscorner.ui.components.PrimaryButton
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }
    var popupTitle by remember { mutableStateOf("") }
    var popupMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> {
                isLoading = true
            }
            is AuthState.PasswordResetEmailSent -> {
                isLoading = false
                popupTitle = "Berhasil"
                popupMessage = "Email reset password telah dikirim. Silakan cek inbox Anda."
                isError = false
                showPopup = true
            }
            is AuthState.Error -> {
                isLoading = false
                popupTitle = "Gagal"
                popupMessage = (authState as AuthState.Error).message
                isError = true
                showPopup = true
            }
            else -> {
                isLoading = false
            }
        }
    }

    // Custom Popup
    if (showPopup) {
        CustomPopup(
            title = popupTitle,
            message = popupMessage,
            isError = isError,
            onDismiss = {
                showPopup = false
                if (!isError) {
                    navController.popBackStack() // kembali ke login jika sukses
                }
            },
            onConfirm = {
                showPopup = false
                if (!isError) {
                    navController.popBackStack() // kembali ke login jika sukses
                }
            },
            confirmText = if (isError) "Coba Lagi" else "OK"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lupa Kata Sandi",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Masukkan email Anda untuk mengatur ulang kata sandi",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        CustomInputField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Kirim Instruksi",
            onClick = {
                if (email.isNotBlank()) {
                    authViewModel.sendPasswordResetEmail(email)
                } else {
                    popupTitle = "Perhatian"
                    popupMessage = "Email tidak boleh kosong"
                    isError = true
                    showPopup = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text(
                text = "Kembali ke Login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}