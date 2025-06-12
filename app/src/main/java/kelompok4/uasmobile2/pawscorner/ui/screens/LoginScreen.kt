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

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate(route = "home") {
                    popUpTo(route = "login") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            is AuthState.EmailNotVerified -> {
                snackbarHostState.showSnackbar("Email belum diverifikasi. Silakan cek email Anda.")
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
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

            Text("Masuk", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Masukkan Email dan Password Anda", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Sembunyikan Password" else "Tampilkan Password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableText(
                    text = AnnotatedString("Lupa Kata Sandi ?"),
                    onClick = { navController.navigate("forgot_password") },
                    style = LocalTextStyle.current.copy(color = Color(0xFF2D5FFF))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authViewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DD39E))
            ) {
                Text("Masuk", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Atau", color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Google & Facebook Buttons (tidak diubah)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Belum punya akun?")
                Spacer(modifier = Modifier.width(4.dp))
                ClickableText(
                    text = AnnotatedString("Daftar"),
                    onClick = { navController.navigate("register") },
                    style = LocalTextStyle.current.copy(color = Color(0xFF2D5FFF))
                )
            }
        }
    }
}
