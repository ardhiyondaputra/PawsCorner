package kelompok4.uasmobile2.pawscorner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kelompok4.uasmobile2.pawscorner.ui.screens.EditProfileScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.EmailVerificationScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.RegisterScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.HomeScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.NotificationScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileDetailScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileScreen
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
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
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("profile_detail") {
            ProfileDetailScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("notification") {
            NotificationScreen(navController)
        }
    }
}