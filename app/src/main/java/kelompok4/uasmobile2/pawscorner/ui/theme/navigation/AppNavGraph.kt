package kelompok4.uasmobile2.pawscorner.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kelompok4.uasmobile2.pawscorner.ui.theme.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.theme.screens.RegisterScreen
import kelompok4.uasmobile2.pawscorner.ui.theme.screens.HomeScreen


@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
    }
}

