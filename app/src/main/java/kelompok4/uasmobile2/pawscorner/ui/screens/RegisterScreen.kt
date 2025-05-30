package kelompok4.uasmobile2.pawscorner.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.data.UserPreferences
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> {
                isLoading = true
            }
            is AuthState.EmailVerificationSent -> {
                isLoading = false
                snackbarHostState.showSnackbar(
                    "Email verifikasi telah dikirim ke $email. Silakan cek email Anda dan klik link verifikasi."
                )
                // Navigate to email verification screen
                navController.navigate("email_verification/$email") {
                    popUpTo("register") { inclusive = false }
                }
            }
            is AuthState.Success -> {
                isLoading = false
                // User berhasil register dan email sudah terverifikasi
                snackbarHostState.showSnackbar("Registrasi berhasil! Silakan login.")
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                isLoading = false
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            else -> {
                isLoading = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
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

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nama Pengguna") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Nomor Telepon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && email.isNotBlank() &&
                        phone.isNotBlank() && password.isNotBlank()) {
                        authViewModel.registerWithEmailVerification(
                            email = email,
                            password = password,
                            username = username,
                            phone = phone
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                enabled = !isLoading && username.isNotBlank() && email.isNotBlank() &&
                        phone.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Daftar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Atau")

            Spacer(modifier = Modifier.height(16.dp))

            // Google & Facebook Buttons (tidak diubah)
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