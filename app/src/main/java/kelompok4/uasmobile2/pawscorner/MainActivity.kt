package kelompok4.uasmobile2.pawscorner

import android.os.Bundle
import android.window.SplashScreen
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
import kelompok4.uasmobile2.pawscorner.data.UserPreferences
import kelompok4.uasmobile2.pawscorner.viewmodel.LoginViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.LoginViewModelFactory
import kelompok4.uasmobile2.pawscorner.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferences = UserPreferences(applicationContext)

        setContent {
            PawsCornerTheme {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(userPreferences)
                )

                MainApp(loginViewModel) // ✅ Ganti dari AppNavGraph ke MainApp
            }
        }
    }
}

@Composable
fun MainApp(loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        val startDest = if (isLoggedIn) "home" else "login"
        AppNavGraph(
            navController = navController,
            loginViewModel = loginViewModel,
            startDestination = startDest
        )
    }
}