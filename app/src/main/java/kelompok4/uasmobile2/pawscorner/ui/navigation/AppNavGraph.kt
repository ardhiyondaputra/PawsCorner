package kelompok4.uasmobile2.pawscorner.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kelompok4.uasmobile2.pawscorner.ui.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.RegisterScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.HomeScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileScreen
import kelompok4.uasmobile2.pawscorner.viewmodel.LoginViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel, // ⬅️ Tambahkan parameter viewModel
    startDestination: String
) {
    // ⬇️ Tambahkan state login
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    // ⬇️ Tentukan start destination berdasarkan login
    val startDestination = if (isLoggedIn) "home" else "login"

    // ⬇️ Buat NavHost dengan startDestination dinamis
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel) // ⬅️ Kirim viewModel
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController, loginViewModel = loginViewModel) // ⬅️ Kirim viewModel
        }

        composable("profile") {
            ProfileScreen(navController, loginViewModel)
        }
    }
}

