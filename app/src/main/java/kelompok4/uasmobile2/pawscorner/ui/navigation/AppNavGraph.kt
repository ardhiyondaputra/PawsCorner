package kelompok4.uasmobile2.pawscorner.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kelompok4.uasmobile2.pawscorner.data.UserPreferences
import kelompok4.uasmobile2.pawscorner.ui.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.RegisterScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.HomeScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileDetailScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileScreen
import kelompok4.uasmobile2.pawscorner.viewmodel.LoginViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.ProfileViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.ProfileViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    startDestination: String,
    userPreferences: UserPreferences
) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val startDestinationFinal = if (isLoggedIn) "home" else "login"

    NavHost(navController = navController, startDestination = startDestinationFinal) {
        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("profile") {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(userPreferences)
            )

            ProfileScreen(
                navController = navController,
                loginViewModel = loginViewModel,
                profileViewModel = profileViewModel
            )
        }
        composable("profile_detail") {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(userPreferences)
            )

            ProfileDetailScreen(
                navController = navController, // <--- Tambahkan ini
                profileViewModel = profileViewModel
            )
        }
    }
}

