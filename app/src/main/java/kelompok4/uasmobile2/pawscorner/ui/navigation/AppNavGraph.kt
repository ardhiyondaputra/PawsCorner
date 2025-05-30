package kelompok4.uasmobile2.pawscorner.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kelompok4.uasmobile2.pawscorner.data.UserPreferences
import kelompok4.uasmobile2.pawscorner.ui.screens.EmailVerificationScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.RegisterScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.HomeScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileDetailScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileScreen
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.ProfileViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.ProfileViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String,
    userPreferences: UserPreferences
) {
    // Jika Anda ingin menggunakan login state dari AuthViewModel, uncomment ini:
    // val authState by authViewModel.authState.collectAsState()
    // val isLoggedIn = authState is AuthState.Success
    // val startDestinationFinal = if (isLoggedIn) "home" else startDestination

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("email_verification/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                navController = navController,
                authViewModel = authViewModel,
                email = email
            )
        }

        composable("home") {
            // Your home screen composable
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("profile") {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(userPreferences)
            )

            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                profileViewModel = profileViewModel
            )
        }
        composable("profile_detail") {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(userPreferences)
            )

            ProfileDetailScreen(
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
    }
}