package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.ui.components.CustomInputField
import kelompok4.uasmobile2.pawscorner.ui.components.CustomPopup
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

    // State untuk popup
    var showPopup by remember { mutableStateOf(false) }
    var popupTitle by remember { mutableStateOf("") }
    var popupMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var shouldNavigateToVerification by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> isLoading = true
            is AuthState.EmailVerificationSent -> {
                isLoading = false
                popupTitle = "Email Verifikasi Terkirim"
                popupMessage = "Email verifikasi telah dikirim ke $email. Silakan cek email Anda."
                isError = false
                shouldNavigateToVerification = true
                showPopup = true
            }
            is AuthState.Success -> {
                isLoading = false
                popupTitle = "Berhasil"
                popupMessage = "Registrasi berhasil! Silakan login."
                isError = false
                shouldNavigateToVerification = false
                showPopup = true
            }
            is AuthState.Error -> {
                isLoading = false
                popupTitle = "Gagal"
                popupMessage = (authState as AuthState.Error).message
                isError = true
                shouldNavigateToVerification = false
                showPopup = true
            }
            else -> isLoading = false
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
                if (shouldNavigateToVerification) {
                    navController.navigate("email_verification/$email") {
                        popUpTo("register") { inclusive = false }
                    }
                } else if (!isError && popupTitle == "Berhasil") {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            },
            onConfirm = {
                showPopup = false
                if (shouldNavigateToVerification) {
                    navController.navigate("email_verification/$email") {
                        popUpTo("register") { inclusive = false }
                    }
                } else if (!isError && popupTitle == "Berhasil") {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            },
            confirmText = "OK"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
            leadingIconVector = Icons.Default.Person,
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
            leadingIconVector = Icons.Default.Email,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            value = phone,
            onValueChange = {
                // Hanya terima angka
                if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                    phone = it
                }
            },
            placeholder = "Nomor Telepon",
            leadingIconVector = Icons.Default.Phone,
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
            leadingIconVector = Icons.Default.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Sembunyikan Password" else "Tampilkan Password"
                    )
                }
            }
        )
        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
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

                // Validasi tambahan
                when {
                    username.isBlank() -> {
                        popupTitle = "Perhatian"
                        popupMessage = "Nama pengguna tidak boleh kosong"
                        isError = true
                        showPopup = true
                    }
                    emailError != null -> {
                        popupTitle = "Perhatian"
                        popupMessage = emailError!!
                        isError = true
                        showPopup = true
                    }
                    phone.isBlank() -> {
                        popupTitle = "Perhatian"
                        popupMessage = "Nomor telepon tidak boleh kosong"
                        isError = true
                        showPopup = true
                    }
                    phone.length < 10 -> {
                        popupTitle = "Perhatian"
                        popupMessage = "Nomor telepon minimal 10 digit"
                        isError = true
                        showPopup = true
                    }
                    passwordError != null -> {
                        popupTitle = "Perhatian"
                        popupMessage = passwordError!!
                        isError = true
                        showPopup = true
                    }
                    else -> {
                        authViewModel.registerWithEmailVerification(
                            email = email,
                            password = password,
                            username = username,
                            phone = phone
                        )
                    }
                }
            },
            enabled = !isLoading,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Atau", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Sudah punya akun? ", color = Color.Gray)
            Text(
                "Masuk",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
    }
}