package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.ui.components.CustomInputField
import kelompok4.uasmobile2.pawscorner.ui.components.PrimaryButton

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // State untuk validasi
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> isLoading = true
            is AuthState.EmailVerificationSent -> {
                isLoading = false
                snackbarHostState.showSnackbar(
                    "Email verifikasi telah dikirim ke $email. Silakan cek email Anda."
                )
                navController.navigate("email_verification/$email") {
                    popUpTo("register") { inclusive = false }
                }
            }
            is AuthState.Success -> {
                isLoading = false
                snackbarHostState.showSnackbar("Registrasi berhasil! Silakan login.")
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                isLoading = false
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            else -> isLoading = false
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.paws_corner_removebg_preview),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(80.dp)
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Daftar", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Buat Akun untuk Memulai!", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            CustomInputField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Nama Pengguna",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = email,
                onValueChange = {
                    email = it
                    // Validasi email sederhana
                    emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() && it.isNotEmpty()) {
                        "Format email tidak sesuai"
                    } else null
                },
                placeholder = "Email",
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(emailError!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "Nomor Telepon",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = password,
                onValueChange = {
                    password = it
                    // Validasi panjang password
                    passwordError = if (it.length < 8 && it.isNotEmpty()) {
                        "Password minimal 8 karakter"
                    } else null
                },
                placeholder = "Password",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Sembunyikan Password" else "Tampilkan Password"
                        )
                    }
                }
            )
            if (passwordError != null) {
                Text(passwordError!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Daftar",
                onClick = {
                    // Validasi sebelum lanjut
                    emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        "Format email tidak sesuai"
                    } else null
                    passwordError = if (password.length < 8) {
                        "Password minimal 8 karakter"
                    } else null

                    if (username.isNotBlank() && emailError == null && passwordError == null &&
                        phone.isNotBlank() && password.isNotBlank()
                    ) {
                        authViewModel.registerWithEmailVerification(
                            email = email,
                            password = password,
                            username = username,
                            phone = phone
                        )
                    }
                },
                enabled = !isLoading,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Atau")

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Sudah punya akun? ")
                Text(
                    "Masuk",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}
