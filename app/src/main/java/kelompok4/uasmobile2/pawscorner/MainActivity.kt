package kelompok4.uasmobile2.pawscorner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import kelompok4.uasmobile2.pawscorner.ui.theme.PawsCornerTheme
import kelompok4.uasmobile2.pawscorner.ui.navigation.AppNavGraph
import androidx.lifecycle.viewmodel.compose.viewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthState
import kelompok4.uasmobile2.pawscorner.ui.screens.SplashScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")

            // Check Firebase Auth instance
            FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth instance: ${true}")

        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
        }

        setContent {
            PawsCornerTheme {
                val authViewModel: AuthViewModel = viewModel()
                val addressViewModel: AddressViewModel = viewModel()
                MainApp(authViewModel, addressViewModel)
            }
        }
    }
}

@Composable
fun MainApp(authViewModel: AuthViewModel, addressViewModel: AddressViewModel){
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val addressViewModel: AddressViewModel = viewModel()
    var showSplash by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }

    // Splash screen dengan loading state
    LaunchedEffect(Unit) {
        try {
            authViewModel.checkAuthState()
            kotlinx.coroutines.delay(2000) // Splash duration
            showSplash = false
            isLoading = false
        } catch (e: Exception) {
            Log.e("MainApp", "Error during initialization", e)
            showSplash = false
            isLoading = false
        }
    }

    when {
        showSplash -> {
            SplashScreen()
        }
        isLoading -> {
            // Optional: Loading screen setelah splash
            SplashScreen() // atau buat LoadingScreen() terpisah
        }
        else -> {
            // Tentukan start destination berdasarkan auth state
            val startDest = when (authState) {
                is AuthState.Success -> {
                    Log.d("MainApp", "User authenticated, navigating to home")
                    "home"
                }
                is AuthState.Error -> {
                    Log.d("MainApp", "Auth error: ${(authState as AuthState.Error).message}")
                    "login"
                }
                else -> {
                    Log.d("MainApp", "No auth state, navigating to login")
                    "login"
                }
            }

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                addressViewModel = addressViewModel,
                startDestination = startDest
            )
        }
    }
}