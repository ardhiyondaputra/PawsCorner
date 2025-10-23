package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.ui.components.CustomInputField
import kelompok4.uasmobile2.pawscorner.ui.components.CustomPopup
import kelompok4.uasmobile2.pawscorner.ui.components.PrimaryButton

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 🔸 State untuk Popup
    var showErrorPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    // 🔸 SMART INDONESIAN ERROR MESSAGES (FIXED)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(route = "home") {
                    popUpTo(route = "login") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                val errorMsg = (authState as AuthState.Error).message.lowercase()
                errorMessage = when {
                    // ❌ SALAH PASSWORD/EMAIL
                    errorMsg.contains("credential") ||
                            errorMsg.contains("password") ||
                            errorMsg.contains("incorrect") ->
                        "Email atau password salah"

                    // ❌ AKUN BELUM TERDAFTAR
                    errorMsg.contains("user-not-found") ||
                            errorMsg.contains("user not found") ||
                            errorMsg.contains("no user") ->
                        "Akun belum terdaftar"

                    // ❌ DEFAULT
                    else -> "Terjadi kesalahan. Coba lagi"
                }
                showErrorPopup = true
            }
            is AuthState.EmailNotVerified -> {
                errorMessage = "Email belum diverifikasi. Silakan cek email Anda."
                showErrorPopup = true
            }
            else -> {}
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.paws_corner_removebg_preview),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Masuk",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Masukkan Email dan Password Anda",
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

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Sembunyikan Password"
                            else "Tampilkan Password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableText(
                    text = AnnotatedString("Lupa Kata Sandi?"),
                    onClick = { navController.navigate("forgot_password") },
                    style = LocalTextStyle.current.copy(color = Color(0xFF2D5FFF))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Masuk",
                onClick = {
                    authViewModel.login(email, password)
                },
                enabled = email.isNotBlank() && password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Atau", color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Belum punya akun? ")
                ClickableText(
                    text = AnnotatedString("Daftar"),
                    onClick = { navController.navigate("register") },
                    style = LocalTextStyle.current.copy(color = Color(0xFF2D5FFF))
                )
            }
        }
    }

    // 🔸 CUSTOM INDONESIAN POPUP
    if (showErrorPopup) {
        CustomPopup(
            title = "Login Gagal",
            message = errorMessage,
            onDismiss = { showErrorPopup = false },
            onConfirm = { showErrorPopup = false },
            confirmText = "Coba Lagi",
            dismissText = "Tutup"
        )
    }
}