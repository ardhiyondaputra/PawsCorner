package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kelompok4.uasmobile2.pawscorner.R
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    email: String
) {
    var isCheckingVerification by remember { mutableStateOf(false) }
    var showResendButton by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by authViewModel.authState.collectAsState()

    // Auto-check verification status setiap 3 detik
    LaunchedEffect(Unit) {
        delay(3000) // Wait 3 seconds before first check
        showResendButton = true

        while (true) {
            authViewModel.checkEmailVerification()
            delay(3000) // Check every 3 seconds
        }
    }

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Email terverifikasi, navigasi ke login
                snackbarHostState.showSnackbar("Email berhasil diverifikasi! Silakan login.")
                navController.navigate("login") {
                    popUpTo("email_verification") { inclusive = true }
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                isCheckingVerification = false
            }
            is AuthState.Loading -> {
                isCheckingVerification = true
            }
            else -> {
                isCheckingVerification = false
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.paws_corner_removebg_preview),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(80.dp)
                    .padding(bottom = 32.dp)
            )

            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFFC107)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verifikasi Email",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kami telah mengirim link verifikasi ke:",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = email,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFC107)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Silakan buka email Anda dan klik link verifikasi. Setelah itu, aplikasi akan otomatis mengarahkan Anda ke halaman login.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isCheckingVerification) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Memeriksa verifikasi...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showResendButton) {
                Button(
                    onClick = {
                        authViewModel.resendEmailVerification()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Resend",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kirim Ulang Email")
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("email_verification") { inclusive = true }
                        popUpTo("register") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali ke Login")
            }
        }
    }
}